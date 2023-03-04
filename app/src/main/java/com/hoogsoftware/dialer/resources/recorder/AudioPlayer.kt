package com.hoogsoftware.dialer.resources.recorder

import java.io.File

interface AudioPlayer {
    fun playFile(file: File)
    fun stop()

}