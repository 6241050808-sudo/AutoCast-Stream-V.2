package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AutoAmber
import com.example.ui.theme.AutoCyan
import com.example.ui.theme.AutoGreen

@Composable
fun KingInstallerGuideScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AutoCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = AutoCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column {
                Text(
                    text = "CarStream & Android Auto Guide",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "คู่มือตั้งค่าแสดงผล YouTube และเว็บเบราว์เซอร์บนจอรถยนต์ ผ่าน KingInstaller",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // USER HELP CENTER: ทำไมไม่ขึ้นหน้าบราวเซอร์หรือยูทูป & ต้องใช้ไฟล์อะไรบ้าง
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("user_faq_troubleshoot_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = androidx.compose.foundation.BorderStroke(2.dp, AutoAmber)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AutoAmber.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AutoAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "ตอบคำถาม: ทำไมไม่ขึ้นจอรถ & ต้องใช้ไฟล์อะไรบ้าง?",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AutoAmber
                            )
                        )
                        Text(
                            text = "คำแนะนำตรงจุดสำหรับแก้ปัญหาหน้าจอมืดหรือไม่ขึ้นแอพ",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                        )
                    }
                }

                // Question 1: ต้องใช้ไฟล์อะไรบ้าง?
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "📁 1. ไฟล์ที่ต้องใช้ (ต้องใช้ไฟล์อะไรเพิ่มหรือไม่?):",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AutoCyan
                            )
                        )
                        Text(
                            text = "• คำตอบ: ไม่ต้องใช้ไฟล์ภายนอกเพิ่มเติมเลยครับ! ตัวระบบ AutoBrowser ได้รวบรวมเอนจินฉายภาพ CarPresentation, MediaBrowserService และระบบแสดงผล YouTube 1080p บรรจุครบถ้วนในไฟล์ APK แล้ว\n" +
                                    "• ตัวอย่างไฟล์ที่คุณส่งมา ได้รับการนำมาตรวจสอบโค้ดและปรับโครงสร้างทั้งหมดให้รองรับระบบรถยนต์เรียบร้อยแล้ว",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE2E8F0),
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                // Question 2: ทำไมถึงไม่ขึ้นหน้าบราวเซอร์หรือยูทูป?
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "🚗 2. ทำไมถึงไม่ขึ้นหน้าบราวเซอร์หรือ YouTube บนจอรถ?:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AutoAmber
                            )
                        )
                        Text(
                            text = "สาเหตุหลักเกิดจากนโยบายความปลอดภัยของ Google Android Auto ที่ปิดกั้นแอปวิดีโอและบราวเซอร์:\n" +
                                    "1) บนมือถือยังไม่ได้เปิด 'แหล่งที่ไม่รู้จัก (Unknown sources)': Google จะซ่อนแอปทันทีหากไม่ได้ติดตั้งจาก Play Store\n" +
                                    "2) ยังไม่ได้กดติ๊กถูกใน 'ปรับแต่งตัวเปิดใช้งาน (Customize Launcher)': ต้องเข้าไปติ๊กเปิดชื่อ AutoBrowser ให้ขึ้นจอรถ\n" +
                                    "3) ยังไม่ได้เปิดสิทธิ์ 'ปรากฏทับแอปอื่น (Display Over Other Apps)': ระบบต้องการสิทธิ์นี้เพื่อฉายภาพเบราว์เซอร์และ YouTube ขึ้นจอวิทยุรถยนต์",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE2E8F0),
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                // Action Buttons for instant fix
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { openAndroidAutoSettings(context) },
                        modifier = Modifier.weight(1f).testTag("quick_fix_aa_settings_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AutoCyan, contentColor = Color(0xFF00363D)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("เปิดตั้งค่า Android Auto", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { openOverlayPermissionSettings(context) },
                        modifier = Modifier.weight(1f).testTag("quick_fix_overlay_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AutoAmber, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("เปิดสิทธิ์ทับแอปอื่น", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // CRITICAL CUSTOMIZE LAUNCHER CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("customize_launcher_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AutoCyan.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AutoCyan)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AutoCyan,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "เปิดใช้งานในเมนู ปรับแต่งตัวเปิดใช้งาน (Customize Launcher)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AutoCyan
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "เมื่อติดตั้งแอพพลิเคชันเรียบร้อยแล้ว ให้ทำตามขั้นตอนนี้บนสมาร์ตโฟนของคุณ:\n\n" +
                            "1. เข้าไปที่ การตั้งค่า (Settings) บนมือถือ > Android Auto\n" +
                            "2. แตะเลือก 'ปรับแต่งตัวเปิดใช้งาน' (Customize launcher)\n" +
                            "3. เลื่อนหาชื่อ 'AutoBrowser' แล้ว แตะติ๊กเครื่องหมายถูก หน้าชื่อแอปเพื่อเปิดการแสดงผล\n\n" +
                            "✓ การตรวจสอบความถูกต้อง: หากติ๊กถูกสำเร็จ ชื่อ AutoBrowser จะย้ายขึ้นมาอยู่ในกลุ่มแอปด้านบนที่พร้อมแสดงบนหน้าจอรถยนต์ทันที!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        openAndroidAutoSettings(context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AutoCyan,
                        contentColor = Color(0xFF00363D)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("open_aa_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Launch,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "เปิดการตั้งค่า Android Auto บนมือถือทันที",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // CRITICAL IN-CAR ERROR FIX CARD: "โหลดรายการที่คุณเลือกไม่ได้"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("troubleshoot_load_error_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AutoAmber.copy(alpha = 0.12f)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AutoAmber)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AutoAmber,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "แก้ปัญหาจอรถขึ้น \"โหลดรายการที่คุณเลือกไม่ได้\"",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AutoAmber
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "หากหน้าจอวิทยุรถยนต์แสดงข้อความ 'โหลดรายการที่คุณเลือกไม่ได้' ให้ตรวจสอบและทำตามดังนี้:\n\n" +
                            "1. ในเวอร์ชันใหม่นี้ ระบบ Media Service ได้รับการปรับปรุงแล้ว เมื่อแตะ AutoBrowser บนจอรถ ระบบจะมีเมนูให้เลือกทันที:\n" +
                            "   • 🎬 เปิด YouTube (1080p 60fps)\n" +
                            "   • 🌐 เว็บเบราว์เซอร์ (Google Web)\n" +
                            "   • 🗺️ แผนที่ Google Maps สด\n" +
                            "   • 🎵 Spotify Web Music\n" +
                            "   • ⚡ รีเฟรชการแสดงผลจอรถยนต์\n\n" +
                            "2. ให้แตะเลือกที่ '🎬 เปิด YouTube' หรือ '🌐 เว็บเบราว์เซอร์' ได้ทันที\n\n" +
                            "3. เปิดสิทธิ์ 'ปรากฏทับแอปอื่น' (Display Over Other Apps): สำหรับส่งหน้าต่างภาพขึ้นจอรถยนต์โดยไม่ถูกระบบ Android บล็อก",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        openOverlayPermissionSettings(context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AutoAmber,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("open_overlay_permission_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "เปิดสิทธิ์ 'ปรากฏทับแอปอื่น' (Display Over Other Apps)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // YouTube & Car Video Playback Feature Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartDisplay,
                        contentDescription = null,
                        tint = AutoAmber
                    )
                    Text(
                        text = "การแสดงผล YouTube & วิดีโอบนหน้าจอรถยนต์ (CarStream Style)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AutoAmber
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                BulletPoint(text = "รองรับ HTML5 Video Fullscreen: เมื่อแตะปุ่มขยายวิดีโอบน YouTube หน้าจอจะเปลี่ยนเป็นโหมดภาพยนตร์เต็มจอแบบไร้ขอบ (Full Screen Video Custom View)")
                BulletPoint(text = "GPU Hardware Acceleration: ประมวลผลผ่านชิปฮาร์ดแวร์เพื่อความคมชัดระดับ Full HD 1080p และภาพนิ่งไหลลื่น 60fps")
                BulletPoint(text = "Unrestricted Audio & Video: ปิดข้อจำกัดการบล็อกเล่นอัตโนมัติด้วย mediaPlaybackRequiresUserGesture = false")
                BulletPoint(text = "สลับ Desktop Mode ได้อิสระ: แตะไอคอนคอมพิวเตอร์ที่แถบด้านบน เพื่อแสดงผลหน้า YouTube แบบเดียวกับคอมพิวเตอร์")
            }
        }

        // Fermata Auto 2.0.2 & Standalone Display Engine Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("guide_fermata_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, AutoGreen)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AutoGreen
                    )
                    Text(
                        text = "ไม่ต้องติดตั้ง Fermata Auto เพิ่มเติม (Standalone Engine)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AutoGreen
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "เหตุผลที่ CarStream เดิมต้องพึ่งพา Fermata Auto 2.0.2:\n" +
                            "• CarStream รุ่นเดิมใช้ไลบรารีเก่าที่ Android Auto รุ่นใหม่บล็อกการเปิดหน้าต่างแสดงผล ทำให้ผู้ใช้ต้องอาศัย Presentation Service และ Mirroring Engine ของ Fermata Auto ในการส่งภาพขึ้นจอรถ\n\n" +
                            "ความสามารถใหม่ของ AutoBrowser:\n" +
                            "✓ เราได้สร้าง AutoBrowserPresentation และ CarDisplayManager ทำงานคู่กับ DisplayManager ในระบบ Android โดยตรง\n" +
                            "✓ เมื่อเชื่อมต่อ Android Auto ระบบจะสร้างหน้าต่างเบราว์เซอร์และวิดีโอ YouTube ฉายขึ้นจอรถยนต์ได้ในตัวเอง 100% โดยไม่ต้องลง Fermata Auto หรือ FS Mirror อีกต่อไป",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        // Why KingInstaller Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("guide_why_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AutoCyan
                    )
                    Text(
                        text = "ทำไมต้องติดตั้งผ่าน KingInstaller (Installer Spoofing)?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AutoCyan
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "เช่นเดียวกับต้นฉบับ CarStream ระบบความปลอดภัยของ Android Auto จะตรวจสอบแหล่งที่มาของตัวติดตั้ง (Installer Package Name) หากติดตั้งเป็น APK ธรรมดา ระบบจะซ่อนแอพ\n\n" +
                            "KingInstaller จะหลอกระบบโดยกำหนดค่าให้ตัวติดตั้งเป็น Google Play Store (com.android.vending) ทำให้ Android Auto ยอมรับว่าแอพติดตั้งถูกต้องและแสดงผลในเมนู Customize Launcher ได้ทันทีโดยไม่ต้องรูทเครื่อง",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        // Step by Step Instructions
        Text(
            text = "ขั้นตอนการติดตั้งและใช้งาน (Installation Steps)",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        GuideStepCard(
            stepNumber = "1",
            title = "ดาวน์โหลด APK ผ่าน GitHub Actions",
            description = "ใน GitHub Repository ของคุณ เข้าไปที่แท็บ 'Actions' > เลือกการทำงานล่าสุด > เลื่อนลงมาที่ Artifacts แล้วดาวน์โหลด AutoBrowser-debug-apk",
            icon = Icons.Default.Download,
            color = AutoCyan
        )

        GuideStepCard(
            stepNumber = "2",
            title = "เปิดโหมดนักพัฒนาบน Android Auto ในสมาร์ตโฟน",
            description = "1. ไปที่การตั้งค่าสมาร์ตโฟน > ค้นหา 'Android Auto'\n" +
                    "2. เลื่อนลงมาที่เวอร์ชัน (Version) แล้วแตะติดต่อกัน 10 ครั้ง จนหน้าจอแสดงข้อความเปิดโหมดนักพัฒนา\n" +
                    "3. แตะจุดสามจุดมุมบนขวา > เลือก 'Developer settings'\n" +
                    "4. ติ๊กเครื่องหมายถูกที่ 'Unknown sources' (แหล่งที่ไม่รู้จัก)",
            icon = Icons.Default.Settings,
            color = AutoAmber
        )

        GuideStepCard(
            stepNumber = "3",
            title = "ติดตั้งไฟล์ APK ด้วย KingInstaller",
            description = "1. เปิดแอพพลิเคชัน KingInstaller ในมือถือ\n" +
                    "2. กดปุ่ม 'Select file' แล้วเลือกไฟล์ APK ของ AutoBrowser ที่ดาวน์โหลดมา\n" +
                    "3. เลือกรูปแบบการติดตั้งเป็น 'LineageOS Package Installer' หรือ 'Default Package Installer'\n" +
                    "4. กดปุ่ม 'Install' เพื่อทำการติดตั้งเสร็จสมบูรณ์",
            icon = Icons.Default.Build,
            color = AutoGreen
        )

        GuideStepCard(
            stepNumber = "4",
            title = "เปิดใช้งานใน Customize Launcher",
            description = "เข้า Android Auto > ปรับแต่งตัวเปิดใช้งาน (Customize launcher) > ติ๊กถูกที่ 'AutoBrowser' เพื่อเลื่อนขึ้นไปแสดงบนจอรถ",
            icon = Icons.Default.CheckCircle,
            color = AutoCyan
        )

        GuideStepCard(
            stepNumber = "5",
            title = "เชื่อมต่อรถยนต์และเปิดใช้งาน AutoBrowser",
            description = "เสียบสาย USB หรือเชื่อมต่อ Android Auto แบบไร้สาย (Wireless AA) สัญลักษณ์ 'AutoBrowser' จะปรากฏขึ้นบนหน้ารายการแอพของจอรถยนต์ สามารถเปิดดู YouTube, Netflix, แผนที่ หรือเว็บไซต์ต่างๆ ได้เต็มหน้าจอ",
            icon = Icons.Default.PlayArrow,
            color = Color(0xFF64B5F6)
        )

        // High Resolution & Video Optimization Tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HighQuality,
                        contentDescription = null,
                        tint = AutoGreen
                    )
                    Text(
                        text = "เทคนิคการตั้งค่าความละเอียดสูง (1080p HD & 60fps)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AutoGreen
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                BulletPoint(text = "Hardware Acceleration: แอพเปิดใช้งานการประมวลผลผ่าน GPU ในตัว เพื่อการเล่นวิดีโอระดับ Full HD ที่เสถียรและลื่นไหลที่สุด")
                BulletPoint(text = "Theater Fullscreen: แตะไอคอนขยายเต็มจอที่แถบด้านบน เพื่อซ่อนเมนูบราวเซอร์และแสดงภาพยนตร์แบบไร้ขอบบนจอรถ")
                BulletPoint(text = "Display Zoom: สามารถปรับขนาดตัวอักษรและหน้าเว็บเป็น 125% - 150% ในหน้า Settings เพื่อให้อ่านง่ายจากระยะเบาะนั่ง")
            }
        }

        // In-Car Safety Disclaimer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AutoAmber.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AutoAmber,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "คำแนะนำความปลอดภัย: แนะนำให้ใช้งานฟังก์ชันการรับชมวิดีโอขณะจอดพักรถ หรือสำหรับผู้โดยสาร เพื่อความปลอดภัยสูงสุดในการขับขี่",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

fun openAndroidAutoSettings(context: Context) {
    try {
        val intent = Intent("com.google.android.projection.gearhead.SETTINGS").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Toast.makeText(context, "กรุณาเลื่อนหาเมนู Android Auto ในการตั้งค่า", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "ไม่สามารถเปิดการตั้งค่าได้โดยตรง: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}

fun openOverlayPermissionSettings(context: Context) {
    try {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "ไม่สามารถเปิดการตั้งค่าสิทธิ์ได้: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun GuideStepCard(
    stepNumber: String,
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AutoGreen,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
        )
    }
}
