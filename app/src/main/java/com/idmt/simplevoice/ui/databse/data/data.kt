package com.idmt.simplevoice.ui.databse.data

enum class SECIONS(val value: Int) {
    Political(1),
    Hindu(2),
    Muslim(3),
    Christian(4),
    YouthANDStudents(5),
    Labour(6),
    Sikh(7),
    Misc(8),

}

fun getAllSecions(): List<SECIONS> {
    return listOf(
        SECIONS.Political,
        SECIONS.Hindu,
        SECIONS.Muslim,
        SECIONS.Christian,
        SECIONS.YouthANDStudents,
        SECIONS.Labour,
        SECIONS.Sikh,
        SECIONS.Misc
    )
}

fun getSection(value: Int): SECIONS? {
    val map = SECIONS.entries.associateBy(SECIONS::value)
    return map[value]
}

