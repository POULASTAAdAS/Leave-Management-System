package com.poulastaa.lms.presentation.download_report

import android.content.Context

sealed interface DownloadReportUiEvent {
    data object OnDepartmentToggle : DownloadReportUiEvent
    data class OnDepartmentChange(val index: Int) : DownloadReportUiEvent

    data object OnLeaveTypeToggle : DownloadReportUiEvent
    data class OnLeaveTypeChange(val index: Int) : DownloadReportUiEvent

    data object OnTeacherToggle : DownloadReportUiEvent
    data class OnTeacherChange(val index: Int) : DownloadReportUiEvent

    data object OnViewReportClick : DownloadReportUiEvent
    data class OnDownloadClick(val context:Context) : DownloadReportUiEvent
}