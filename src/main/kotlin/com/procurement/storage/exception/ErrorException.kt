package com.procurement.storage.exception

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.procurement.storage.utils.nowUTC


@JsonPropertyOrder("timestamp", "status", "error", "message", "path")
class ErrorException(msg: String, path: String) {

    var timestamp = nowUTC()
    var status = 400
    var error = "Bad Request"
    var message = msg
    var path = path
}
