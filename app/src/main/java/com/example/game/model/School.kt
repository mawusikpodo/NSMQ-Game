package com.example.game.model

data class School(
    val id: String,
    val name: String,
    val shortName: String,
    val moniker: String,
    val titles: Int,
    val region: String,
    val emoji: String
)

object SchoolCatalog {
    val schools: List<School> = listOf(
        School(
            id = "presec",
            name = "Presbyterian Boys' Secondary School",
            shortName = "PRESEC Legon",
            moniker = "Blue Magicians 🦁",
            titles = 8,
            region = "Greater Accra",
            emoji = "🦁"
        ),
        School(
            id = "prempeh",
            name = "Prempeh College",
            shortName = "Prempeh College",
            moniker = "Amanfuo 🦅",
            titles = 5,
            region = "Ashanti",
            emoji = "🦅"
        ),
        School(
            id = "persco",
            name = "St. Peter's Boys Senior High",
            shortName = "PERSCO Nkwatia",
            moniker = "The Mountain Boys 🏔️",
            titles = 3,
            region = "Eastern",
            emoji = "🏔️"
        ),
        School(
            id = "mfantsipim",
            name = "Mfantsipim School",
            shortName = "Mfantsipim",
            moniker = "Kwabotwe 🐘",
            titles = 2,
            region = "Central",
            emoji = "🐘"
        ),
        School(
            id = "augusco",
            name = "St. Augustine's College",
            shortName = "AUGUSCO",
            moniker = "APSU Boys 🟢",
            titles = 2,
            region = "Central",
            emoji = "🟢"
        ),
        School(
            id = "owass",
            name = "Opoku Ware School",
            shortName = "OWASS Kumasi",
            moniker = "Akatakyie ⚔️",
            titles = 2,
            region = "Ashanti",
            emoji = "⚔️"
        ),
        School(
            id = "motown",
            name = "Achimota School",
            shortName = "Achimota School",
            moniker = "Motown 🎹",
            titles = 2,
            region = "Greater Accra",
            emoji = "🎹"
        ),
        School(
            id = "weygayhey",
            name = "Wesley Girls' High School",
            shortName = "Wey Gey Hey",
            moniker = "Live Pure 🟡",
            titles = 0,
            region = "Central",
            emoji = "🟡"
        ),
        School(
            id = "ketashts",
            name = "Keta Senior High Technical",
            shortName = "Ketasco",
            moniker = "Dzo Lali Eagles 🦅",
            titles = 0,
            region = "Volta",
            emoji = "⚡"
        ),
        School(
            id = "pojoss",
            name = "Pope John SHS & Minor Seminary",
            shortName = "POJOSS Koforidua",
            moniker = "Daasebre 🕊️",
            titles = 1,
            region = "Eastern",
            emoji = "🕊️"
        ),
        School(
            id = "adisco",
            name = "Adisadel College",
            shortName = "Adisadel",
            moniker = "Santaclausians 🦓",
            titles = 1,
            region = "Central",
            emoji = "🦓"
        ),
        School(
            id = "tamasco",
            name = "Tamale Senior High School",
            shortName = "TAMASCO",
            moniker = "The Pride of the North 🌴",
            titles = 0,
            region = "Northern",
            emoji = "🌴"
        ),
        School(
            id = "abugiss",
            name = "Aburi Girls' Senior High",
            shortName = "Aburi Girls",
            moniker = "Bepow So Hann 🏔️",
            titles = 0,
            region = "Eastern",
            emoji = "✨"
        ),
        School(
            id = "holico",
            name = "Holy Child School",
            shortName = "Holy Child",
            moniker = "Holico 🌸",
            titles = 0,
            region = "Central",
            emoji = "🌸"
        ),
        School(
            id = "ghsnat",
            name = "Ghana National College",
            shortName = "Ghana National",
            moniker = "Nananom 🛡️",
            titles = 0,
            region = "Central",
            emoji = "🛡️"
        ),
        School(
            id = "aquinas",
            name = "St. Thomas Aquinas SHS",
            shortName = "Aquinas",
            moniker = "Old Saints 🌟",
            titles = 1,
            region = "Greater Accra",
            emoji = "🌟"
        )
    )

    fun findSchool(query: String): School? {
        return schools.firstOrNull {
            it.shortName.equals(query, ignoreCase = true) ||
            it.name.contains(query, ignoreCase = true) ||
            it.id.equals(query, ignoreCase = true)
        }
    }
}
