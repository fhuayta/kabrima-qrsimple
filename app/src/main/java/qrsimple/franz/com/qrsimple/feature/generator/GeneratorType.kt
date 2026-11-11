package qrsimple.franz.com.qrsimple.feature.generator

import qrsimple.franz.com.qrsimple.R

enum class GeneratorType(val labelRes: Int) {
    URL(R.string.generator_tab_url),
    WIFI(R.string.generator_tab_wifi),
    TEXT(R.string.generator_tab_text),
    WHATSAPP(R.string.generator_tab_whatsapp),
    EMAIL(R.string.generator_tab_email)
}
