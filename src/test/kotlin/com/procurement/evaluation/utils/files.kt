package com.procurement.evaluation.utils

import java.io.File

fun readFile(fileName: String): String = File(fileName).readText(Charsets.UTF_8)