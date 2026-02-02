package com.neko.music.data.manager

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * 版本信息 JSON 数据类
 */
@Serializable
data class VersionResponse(
    val ver: String,
    val updateUrl: String
)

/**
 * 应用更新信息
 */
data class UpdateInfo(
    val versionName: String,
    val versionCode: Int,
    val updateUrl: String,
    val isUpdateAvailable: Boolean
)

/**
 * 应用更新管理器
 */
class AppUpdateManager(private val context: Context) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    private val client = HttpClient(OkHttp) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(json)
        }
    }
    
    private val versionCheckUrl = "https://music.cnmsb.xin/version.json"
    
    /**
     * 获取当前应用版本信息
     */
    fun getCurrentVersion(): Pair<String, Int> {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )
            val versionName = packageInfo.versionName ?: "1.0.0"
            val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                packageInfo.versionCode
            }
            Pair(versionName, versionCode)
        } catch (e: Exception) {
            Log.e("AppUpdateManager", "获取当前版本失败", e)
            Pair("1.0.0", 1)
        }
    }
    
    /**
     * 检查更新
     */
    suspend fun checkUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d("AppUpdateManager", "开始检查更新...")
            val response: VersionResponse = client.get(versionCheckUrl).body()
            val currentVersion = getCurrentVersion()
            val currentVersionName = currentVersion.first
            val currentVersionCode = currentVersion.second
            
            val versionName = response.ver
            val updateUrl = response.updateUrl
            
            Log.d("AppUpdateManager", "当前版本: $currentVersionName ($currentVersionCode)")
            Log.d("AppUpdateManager", "服务器版本: $versionName")
            Log.d("AppUpdateManager", "更新URL: $updateUrl")
            
            // 提取 versionCode（括号中的数字）
            val versionCode = extractVersionCode(versionName)
            
            Log.d("AppUpdateManager", "提取的版本号: $versionCode")
            
            // 判断是否需要更新：两个版本数据都必须比当前版本新
            val isUpdateAvailable = versionCode > currentVersionCode
            
            Log.d("AppUpdateManager", "是否需要更新: $isUpdateAvailable")
            
            UpdateInfo(
                versionName = versionName,
                versionCode = versionCode,
                updateUrl = updateUrl,
                isUpdateAvailable = isUpdateAvailable
            )
        } catch (e: Exception) {
            Log.e("AppUpdateManager", "检查更新失败", e)
            null
        }
    }    
    /**
     * 从 versionName 中提取 versionCode
     * 支持多种格式：
     * - versionName(versionCode)
     * - versionName-VERSIONCODE
     * - versionName-BETA-VERSIONCODE
     */
    private fun extractVersionCode(versionName: String): Int {
        // 尝试多种提取方式
        val patterns = listOf(
            "\\((\\d+)\\)".toRegex(),  // (20)
            "-(\\d+)$".toRegex(),         // -21
            "-BETA-(\\d+)$".toRegex(),    // -BETA-21
            "-RC-(\\d+)$".toRegex(),      // -RC-21
            "-ALPHA-(\\d+)$".toRegex()    // -ALPHA-21
        )
        
        for (pattern in patterns) {
            val match = pattern.find(versionName)
            if (match != null) {
                return match.groupValues.get(1)?.toIntOrNull() ?: 1
            }
        }
        
        // 如果都不匹配，尝试从末尾提取所有数字
        val lastDash = versionName.lastIndexOf('-')
        if (lastDash != -1) {
            val afterDash = versionName.substring(lastDash + 1)
            val number = afterDash.toIntOrNull()
            if (number != null) {
                return number
            }
        }
        
        Log.w("AppUpdateManager", "无法从版本名中提取版本号: $versionName")
        return 1
    }
    
    /**
     * 下载 APK 文件
     */
    suspend fun downloadApk(
        url: String,
        onProgress: (Long, Long) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = client.get(url)
            val contentLength = response.headers["Content-Length"]?.firstOrNull()?.toLong() ?: 0L
            
            val apkFile = File(context.getExternalFilesDir(null), "update.apk")
            
            val bytes = response.body<ByteArray>()
            apkFile.writeBytes(bytes)
            
            onProgress(bytes.size.toLong(), contentLength)
            
            apkFile
        } catch (e: Exception) {
            Log.e("AppUpdateManager", "下载 APK 失败", e)
            null
        }
    }
    
    /**
     * 检查是否有安装权限
     */
    fun canInstallPackages(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }
    
    /**
     * 安装 APK
     */
    fun installApk(apkFile: File) {
        try {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                setDataAndType(
                    uri,
                    "application/vnd.android.package-archive"
                )
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("AppUpdateManager", "安装 APK 失败", e)
        }
    }
}