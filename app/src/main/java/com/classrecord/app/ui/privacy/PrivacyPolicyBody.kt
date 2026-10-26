package com.classrecord.app.ui.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

internal data class PrivacySection(
    val title: String = "",
    val paragraphs: List<String> = emptyList(),
    val bullets: List<String> = emptyList(),
)

@Composable
fun PrivacyPolicyBody(english: Boolean, modifier: Modifier = Modifier) {
    val sections = if (english) englishSections() else chineseSections()
    Column(modifier.padding(vertical = 4.dp)) {
        sections.forEachIndexed { index, section ->
            if (index > 0) Spacer(Modifier.height(20.dp))
            if (section.title.isNotBlank()) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
            }
            section.paragraphs.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(8.dp))
            }
            section.bullets.forEach { bullet ->
                Text(
                    text = "• $bullet",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                )
            }
        }
    }
}

private fun chineseSections(): List<PrivacySection> = listOf(
    PrivacySection(
        title = "一句话说明",
        paragraphs = listOf(
            "团团记是离线、无账号、无服务器的工具。默认不收集、不上传你的个人信息。" +
                "数据只存在你手机本机；只有你主动导出、分享或备份时，相关文件才会离开本机。",
        ),
    ),
    PrivacySection(
        title = "本应用处理哪些数据",
        paragraphs = listOf("1. 应用内业务数据（只存本机）"),
        bullets = listOf(
            "团体名称与成员名单（含可选编号、备注、归档状态）",
            "小团体划分与成员归属",
            "出勤、缴费、费用分摊、清单等事务及进度快照",
            "团费账本流水与余额",
            "你为缴费或分摊主动添加的备注与凭证图片（若你选择添加）",
            "界面语言、主题、名单排序等偏好设置",
        ),
    ),
    PrivacySection(
        paragraphs = listOf(
            "上述数据保存在手机本机的应用存储中，不会同步到我们运营的云端（我们没有这类服务器）。",
            "2. 你主动导出或分享的内容",
            "当你使用「备份 / 恢复 / 导出 CSV / 复制催缴文案 / 分享」等功能时，会生成 zip、CSV、文本或图片等文件。" +
                "这些文件只在你选择的保存位置或分享目标中出现。我们不会代你上传。",
            "3. 我们不收集的内容",
        ),
        bullets = listOf(
            "不要求注册或登录",
            "不收集姓名、手机号、邮箱、身份证等身份信息",
            "不接入广告、统计分析或崩溃上报 SDK",
            "本应用未声明联网（INTERNET）权限；我们也没有自建后端来接收你的业务数据",
        ),
    ),
    PrivacySection(
        title = "权限说明",
        paragraphs = listOf("以安装时系统展示及当前版本合并清单为准："),
        bullets = listOf(
            "主清单未声明 INTERNET 等联网权限",
            "AndroidX 可能声明内部权限（如 DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION），不用于上传你的数据",
            "若你使用「拍照」或「从相册选择」添加凭证，由系统在你要操作时提供；本应用不会在后台扫描相册上传",
            "备份、导出、分享通过系统文档选择器或分享面板，由你主动操作",
        ),
    ),
    PrivacySection(
        title = "儿童与未成年人、数据安全与换机",
        paragraphs = listOf(
            "本应用不面向儿童定向收集信息；监护人可在设备上管理或卸载应用。",
            "本机数据安全主要取决于你的设备锁屏与系统设置。卸载通常会删除应用内数据；换机或重装前请先备份。",
            "不同签名版本可能无法互相覆盖安装；强制卸载会丢失数据，请先备份。",
        ),
    ),
    PrivacySection(
        title = "第三方与开源、政策变更",
        paragraphs = listOf(
            "从 GitHub Releases、酷安、Google Play 等渠道下载时，该渠道可能有各自的下载与账号政策。",
            "开源库按其许可证使用；我们不会借此引入追踪类服务。",
            "若本政策有实质变更，我们会更新生效日期，并在合理范围内通过官网或应用更新说明提示。",
        ),
    ),
    PrivacySection(
        title = "联系方式",
        paragraphs = listOf(
            "隐私相关问题请通过 hi@etais.dev、https://etais.dev/ 或 GitHub 仓库 gnatecheng/group-matters 的 Issues 联系发布者。",
            "生效日期：2026-10-13 · 发布者：Ch Etang（Etai 应用集）",
        ),
    ),
)

private fun englishSections(): List<PrivacySection> = listOf(
    PrivacySection(
        title = "In one sentence",
        paragraphs = listOf(
            "Group Matters is offline, account-free, and server-free. By default we do not collect or upload personal information. " +
                "Data stays on your device; files leave the phone only when you export, share, or back up yourself.",
        ),
    ),
    PrivacySection(
        title = "What this app processes",
        paragraphs = listOf("1. In-app data (on-device only)"),
        bullets = listOf(
            "Group name and member roster (optional member ID, notes, archive state)",
            "Sub-groups and membership",
            "Attendance, payments, expense splits, and checklists, including roster snapshots",
            "Group fund ledger entries and balance",
            "Notes and payment proof images you optionally attach",
            "UI preferences such as language, theme, and roster sort order",
        ),
    ),
    PrivacySection(
        paragraphs = listOf(
            "This data is stored in the app's private on-device storage. It is not synced to a cloud service we operate.",
            "2. Content you export or share",
            "When you back up, restore, export CSV, copy reminder text, or share files, those files appear only where you choose to save or send them.",
            "3. What we do not collect",
        ),
        bullets = listOf(
            "No sign-up or sign-in",
            "No identity data such as name, phone number, email, or ID numbers for our services",
            "No ad, analytics, or crash-reporting SDKs",
            "This app does not declare the INTERNET permission; we have no backend that receives your app data",
        ),
    ),
    PrivacySection(
        title = "Permissions",
        paragraphs = listOf("See the system install prompt and the merged manifest for your build:"),
        bullets = listOf(
            "The main manifest does not request INTERNET or other network access",
            "AndroidX may declare internal permissions such as DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION; they are not used to upload your data",
            "If you take a photo or pick from the gallery for a proof, the system provides that at your request; we do not scan your gallery in the background",
            "Backup, export, and share use the system document picker or share sheet at your initiative",
        ),
    ),
    PrivacySection(
        title = "Children, security, and device changes",
        paragraphs = listOf(
            "We do not direct this app at children; a parent or guardian can manage or uninstall the app on the device.",
            "On-device security depends mainly on your lock screen and system settings. Uninstalling usually deletes app data; back up before reinstalling or switching phones.",
            "Release builds signed with a different key may not overwrite older installs unless you back up first.",
        ),
    ),
    PrivacySection(
        title = "Third parties, open source, and changes",
        paragraphs = listOf(
            "Download stores have their own policies separate from in-app data handling.",
            "Open-source libraries are used under their licenses; we do not use them to add tracking services.",
            "If this policy changes in a material way, we will update the effective date and note it where practical.",
        ),
    ),
    PrivacySection(
        title = "Contact",
        paragraphs = listOf(
            "For privacy questions, contact the publisher at hi@etais.dev, https://etais.dev/, or via Issues on the group-matters GitHub repository.",
            "Effective date: 2026-10-13 · Publisher: Ch Etang (Etai Apps)",
        ),
    ),
)
