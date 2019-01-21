package com.procurement.storage.model

object MIMEConverter {
    operator fun get(fileExtension: String, default: String = "application/octet-stream"): String {
        return when (fileExtension) {
            /*
             * Images
             */
            "jpg", "jpeg", "jpe" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "tif", "tiff" -> "image/tiff"

            /*
             * Archives
             */
            "rar" -> "application/x-rar-compressed"
            "zip" -> "application/zip"
            "7z" -> "application/x-7z-compressed"

            /*
             * Documents
             */
            "doc", "docx" -> "application/msword"
            "xls", "xlsx" -> "application/vnd.ms-excel"
            "rtf" -> "application/rtf"
            "txt" -> "text/plain"
            "pdf" -> "application/pdf"
            else -> default
        }
    }

    fun extension(filename: String): String = filename.substringAfterLast('.', "")
}