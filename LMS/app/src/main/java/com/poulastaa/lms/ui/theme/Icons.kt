package com.poulastaa.lms.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import com.poulastaa.lms.R

val AppLogo: Painter
    @Composable
    get() = painterResource(id = R.drawable.ic_app_logo)

val EmailIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_email)

val CheckIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_check)

val UserIcon: ImageVector
    @Composable
    get() = Icons.Rounded.Person

val ArrowBackIcon: ImageVector
    @Composable
    get() = Icons.AutoMirrored.Rounded.ArrowBack

val PhoneIcon: ImageVector
    @Composable
    get() = Icons.Rounded.Phone

val CalenderIcon: ImageVector
    @Composable
    get() = Icons.Rounded.DateRange

val CakeIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_cake)

val ArrowDropDownIcon: ImageVector
    @Composable
    get() = Icons.Rounded.ArrowDropDown

val ProfileMaleIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_profile_male)

val ProfileFemaleIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_profile_female)

val ApplyLeaveIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_apply_leave)

val LeaveStatusIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_leave_status)

val LeaveHistoryIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_leave_history)

val ViewLeaveIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_view_leave)

val ApproveLeaveIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_approve_leave)

val LeaveReportIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_leave_report)

val DepartmentInChargeIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_department_in_charge)

val AddIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_add)

val EditIcon: ImageVector
    @Composable
    get() = Icons.Rounded.Edit

val OutlineEmailIcon: ImageVector
    @Composable
    get() = Icons.Outlined.Email


val OutlinePhoneIcon: ImageVector
    @Composable
    get() = Icons.Outlined.Phone


val OutlineQualificationIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_outline_qualification)


val OutlineHouseIcon: ImageVector
    @Composable
    get() = Icons.Outlined.Home


val OutlineDepartmentIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_outline_department)


val OutlineJoiningDateIcon: ImageVector
    @Composable
    get() = Icons.Outlined.DateRange


val OutlineExpIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_outline_exp)
