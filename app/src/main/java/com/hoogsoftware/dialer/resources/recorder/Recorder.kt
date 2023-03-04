package com.hoogsoftware.dialer.resources.recorder

import java.io.File

interface Recorder {
    fun start(file: File)
    fun stop()

}