package com.neko.music.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neko.music.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onPasswordResetSuccess: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSendingCode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var codeSent by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(0) }

    // 动画状态
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 150, delayMillis = 30),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // 倒计时
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown--
        }
    }

    val scope = rememberCoroutineScope()
    val userApi = com.neko.music.data.api.UserApi(context = context)

    // Preload string resources
    val pleaseEnterEmail = stringResource(id = R.string.enter_email)
    val pleaseEnterVerificationCode = stringResource(id = R.string.verification_code)
    val pleaseEnterNewPassword = stringResource(id = R.string.new_password_hint)
    val passwordLengthError = stringResource(id = R.string.password_length_error)
    val passwordMismatch = stringResource(id = R.string.password_mismatch)
    val emailFormatError = stringResource(id = R.string.email_format_error)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
            .windowInsetsPadding(WindowInsets(0.dp))
    ) {
        // 处理返回键
        BackHandler {
            onBackClick()
        }

        // 返回按钮
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis = 150, delayMillis = 60)
                                    ) + fadeIn(animationSpec = tween(durationMillis = 150, delayMillis = 60))        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 80.dp)
                .scale(scale)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // 标题
            Text(
                text = stringResource(id = R.string.reset_password),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = stringResource(id = R.string.reset_password_email_hint),
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 邮箱输入框
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 150, delayMillis = 90)
                                        ) + fadeIn(animationSpec = tween(durationMillis = 150, delayMillis = 90))            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = ""
                    },
                    label = { Text(stringResource(id = R.string.email)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE94560),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedContainerColor = Color(0xFF1A1A2E)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = !isSendingCode && !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 验证码输入框
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 150, delayMillis = 120)
                                        ) + fadeIn(animationSpec = tween(durationMillis = 150, delayMillis = 120))            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = verificationCode,
                        onValueChange = {
                            verificationCode = it
                            errorMessage = ""
                        },
                        label = { Text(stringResource(id = R.string.verification_code)) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE94560),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            unfocusedContainerColor = Color(0xFF1A1A2E)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        enabled = codeSent && !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Button(
                        onClick = {
                            if (email.isEmpty()) {
                                errorMessage = pleaseEnterEmail
                                return@Button
                            }

                            // 验证邮箱格式
                            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                            if (!emailRegex.matches(email)) {
                                errorMessage = emailFormatError
                                return@Button
                            }

                            isSendingCode = true
                            scope.launch {
                                try {
                                    val response = userApi.sendForgotPasswordCode(email)
                                    isSendingCode = false

                                    if (response.success) {
                                        codeSent = true
                                        countdown = 60
                                        errorMessage = ""
                                        android.widget.Toast.makeText(
                                            context,
                                            context.getString(R.string.verification_code_sent),
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        errorMessage = response.message
                                    }
                                } catch (e: Exception) {
                                    isSendingCode = false
                                    errorMessage = context.getString(R.string.send_verification_code_failed, e.message ?: "")
                                }
                            }
                        },
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE94560)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSendingCode && countdown == 0 && !isLoading
                    ) {
                        if (isSendingCode) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (countdown > 0) {
                                    stringResource(id = R.string.retry_after_seconds, countdown)
                                } else {
                                    stringResource(id = R.string.send_reset_code)
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 新密码输入框
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 150, delayMillis = 150)
                                        ) + fadeIn(animationSpec = tween(durationMillis = 150, delayMillis = 150))            ) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        errorMessage = ""
                    },
                    label = { Text(stringResource(id = R.string.new_password_hint)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE94560),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedContainerColor = Color(0xFF1A1A2E)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = codeSent && !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 确认新密码输入框
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 150, delayMillis = 180)
                                        ) + fadeIn(animationSpec = tween(durationMillis = 150, delayMillis = 180))            ) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        errorMessage = ""
                    },
                    label = { Text(stringResource(id = R.string.confirm_new_password_hint)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE94560),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        unfocusedContainerColor = Color(0xFF1A1A2E)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    enabled = codeSent && !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }

            // 错误提示
            AnimatedVisibility(
                visible = errorMessage.isNotEmpty(),
                enter = expandVertically(animationSpec = tween(durationMillis = 150)) + fadeIn(),
                                    exit = shrinkVertically(animationSpec = tween(durationMillis = 150)) + fadeOut()            ) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 重置密码按钮
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 150, delayMillis = 210)
                                        ) + fadeIn(animationSpec = tween(durationMillis = 150, delayMillis = 210))            ) {
                Button(
                    onClick = {
                        // 验证
                        if (email.isEmpty()) {
                            errorMessage = pleaseEnterEmail
                            return@Button
                        }

                        // 验证邮箱格式
                        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                        if (!emailRegex.matches(email)) {
                            errorMessage = emailFormatError
                            return@Button
                        }

                        if (!codeSent || verificationCode.isEmpty()) {
                            errorMessage = pleaseEnterVerificationCode
                            return@Button
                        }

                        if (newPassword.isEmpty()) {
                            errorMessage = pleaseEnterNewPassword
                            return@Button
                        }

                        if (newPassword.length < 6) {
                            errorMessage = passwordLengthError
                            return@Button
                        }

                        if (newPassword != confirmPassword) {
                            errorMessage = passwordMismatch
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            try {
                                val response = userApi.resetPassword(email, verificationCode, newPassword)
                                isLoading = false

                                if (response.success) {
                                    android.widget.Toast.makeText(
                                        context,
                                        context.getString(R.string.password_modified_login_again),
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    onPasswordResetSuccess()
                                } else {
                                    errorMessage = response.message
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = context.getString(R.string.reset_password_failed, e.message ?: "")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE94560)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading && codeSent
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.reset_password),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}