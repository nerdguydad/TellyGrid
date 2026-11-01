package io.github.lozza.tellygrid.data

/**
 * Start.ca Start TV channel lineup on Fire TV — generated from the captured
 * channel sweep (mediaId per channel) + the Schedules Direct stationID map.
 *
 * `channelUri` is repurposed to carry the Start TV media id
 * (`LIVE$STARTCA-<num>-<slug>` or bare `LIVE$<num>`). AppHandoffLauncher's
 * START_TV branch turns it into an `intent_extra_data_key` tune intent via
 * StartTvProgrammeLink. `xmlTvId` is the Gracenote stationID so the nexus
 * SD->XMLTV feed's <channel id="..."> lines up with each row.
 */
object StartTvChannelCatalog {
    private fun channel(
        id: String,
        number: Int,
        name: String,
        mediaId: String,
        stationId: String,
        category: GuideCategory,
    ) = CatalogChannel(
        id = id,
        xmlTvId = stationId,
        number = number,
        displayName = name,
        providerLabel = "Start.ca",
        accentArgb = 0xFF4FC3F7L,
        logoUri = null,
        provider = ProviderRegistry.startTv,
        channelUri = mediaId,
        terrestrialLcn = null,
        category = category,
    )

    val channels: List<CatalogChannel> = listOf(
        channel("cwea", 110, "Weather Network", "LIVE\$STARTCA-8615-WEA", "10170", GuideCategory.ENTERTAINMENT),
        channel("ciiidt", 111, "Global Toronto", "LIVE\$STARTCA-38319-GLB-T", "44792", GuideCategory.ENTERTAINMENT),
        channel("chandt", 113, "Global BC", "LIVE\$STARTCA-36758-GLB-V", "60121", GuideCategory.ENTERTAINMENT),
        channel("cbotdt", 115, "CBC Ottawa", "LIVE\$STARTCA-38326-CBCOH", "58494", GuideCategory.ENTERTAINMENT),
        channel("cbltdt", 116, "CBC Toronto", "LIVE\$STARTCA-38109-CBCHE", "46245", GuideCategory.ENTERTAINMENT),
        channel("chex", 117, "Global Peterborough", "LIVE\$STARTCA-38323-CHEX", "10114", GuideCategory.ENTERTAINMENT),
        channel("cbet", 118, "CBC Windsor", "LIVE\$STARTCA-38327-CBCWH", "10088", GuideCategory.ENTERTAINMENT),
        channel("cbutdt", 119, "CBC Vancouver", "LIVE\$STARTCA-38110-CBCHW", "51981", GuideCategory.ENTERTAINMENT),
        channel("cfmtdt", 121, "OMNI1", "LIVE\$STARTCA-38316-OM1HD", "44788", GuideCategory.ENTERTAINMENT),
        channel("cjmtdt", 122, "OMNI2", "LIVE\$STARTCA-38324-OM2HD", "44790", GuideCategory.ENTERTAINMENT),
        channel("omni", 123, "OMNI East", "LIVE\$STARTCA-29452-OMNI-E", "105354", GuideCategory.ENTERTAINMENT),
        channel("cp24hd", 124, "CP24", "LIVE\$STARTCA-33505-CP24H", "94289", GuideCategory.NEWS),
        channel("cjohdt", 126, "CTV Ottawa", "LIVE\$STARTCA-38311-CTV-O", "68025", GuideCategory.ENTERTAINMENT),
        channel("chrodt", 127, "CTV2 Ottawa", "LIVE\$STARTCA-38315-CTV2O", "72715", GuideCategory.ENTERTAINMENT),
        channel("cftodt", 128, "CTV Toronto", "LIVE\$STARTCA-36759-CTVHT", "44784", GuideCategory.ENTERTAINMENT),
        channel("ckvrdt", 129, "CTV2 Barrie", "LIVE\$STARTCA-38314-CTV2T", "72705", GuideCategory.ENTERTAINMENT),
        channel("ckcodt", 131, "CTV Kitchener", "LIVE\$STARTCA-38313-CTV-K", "72614", GuideCategory.ENTERTAINMENT),
        channel("cfpldt", 132, "CTV2 London", "LIVE\$STARTCA-38312-CTV2L", "72720", GuideCategory.ENTERTAINMENT),
        channel("chwidt", 133, "CTV2 Windsor", "LIVE\$STARTCA-38916-CTV2W", "72956", GuideCategory.ENTERTAINMENT),
        channel("chchdt", 136, "CHCH", "LIVE\$STARTCA-38310-CHCH", "60123", GuideCategory.ENTERTAINMENT),
        channel("ckws", 137, "Global Kingston", "LIVE\$STARTCA-38328-GLK-B", "10137", GuideCategory.ENTERTAINMENT),
        channel("civtdt", 139, "CTV Vancouver", "LIVE\$STARTCA-38107-CTVHW", "44786", GuideCategory.ENTERTAINMENT),
        channel("citydt", 140, "Citytv Toronto", "LIVE\$STARTCA-38105-CTY-T", "34200", GuideCategory.KIDS),
        channel("cicadt", 141, "TVO", "LIVE\$STARTCA-13374-TVO", "70583", GuideCategory.ENTERTAINMENT),
        channel("cftmdt", 142, "TVA Montreal", "LIVE\$STARTCA-37325-TV5HD", "72755", GuideCategory.ENTERTAINMENT),
        channel("tfo", 145, "TFO", "LIVE\$STARTCA-38445-TFO", "10109", GuideCategory.ENTERTAINMENT),
        channel("unis", 146, "Unis TV", "LIVE\$STARTCA-38322-UNIS", "90112", GuideCategory.ENTERTAINMENT),
        channel("rdihd", 150, "ICI RDI", "LIVE\$STARTCA-38113-RDIHD", "60327", GuideCategory.ENTERTAINMENT),
        channel("cbftdt", 151, "Ici Montreal", "LIVE\$STARTCA-9281-CBFT", "45867", GuideCategory.ENTERTAINMENT),
        channel("amiau", 160, "AMI-audio", "LIVE\$STARTCA-37421-AMIA", "20783", GuideCategory.ENTERTAINMENT),
        channel("amitv", 161, "AMI-tv", "LIVE\$STARTCA-37356-AMITV", "62670", GuideCategory.ENTERTAINMENT),
        channel("amitele", 162, "AMI-tele", "LIVE\$STARTCA-38321-AMITL", "91109", GuideCategory.ENTERTAINMENT),
        channel("wpixdt", 171, "WPIX NYC", "LIVE\$STARTCA-32688-WPIX-DT", "20373", GuideCategory.ENTERTAINMENT),
        channel("wgbhdt", 175, "PBS Boston", "LIVE\$38283", "28055", GuideCategory.ENTERTAINMENT),
        channel("wcvbdt", 181, "ABC Boston", "LIVE\$STARTCA-38281-ABC-E", "19596", GuideCategory.ENTERTAINMENT),
        channel("wbzdt", 182, "CBS Boston", "LIVE\$STARTCA-38280-CBSHE", "20431", GuideCategory.ENTERTAINMENT),
        channel("wfxtdt", 183, "FOX Boston", "LIVE\$STARTCA-38282-FOX-E", "20362", GuideCategory.ENTERTAINMENT),
        channel("wbtscd", 184, "NBC Boston", "LIVE\$STARTCA-38279-NBC-E", "91446", GuideCategory.ENTERTAINMENT),
        channel("komodt", 189, "ABC Seattle", "LIVE\$STARTCA-38097-ABC-W", "19629", GuideCategory.ENTERTAINMENT),
        channel("kirodt", 190, "CBS Seattle", "LIVE\$STARTCA-38100-CBS-W", "20290", GuideCategory.ENTERTAINMENT),
        channel("kcpqdt", 191, "FOX Tacoma", "LIVE\$STARTCA-38102-FOX-W", "21254", GuideCategory.ENTERTAINMENT),
        channel("kcpqdt", 192, "FOX Tacoma", "LIVE\$STARTCA-38102-FOX-W", "21254", GuideCategory.ENTERTAINMENT),
        channel("artschd", 201, "A&E Canada", "LIVE\$STARTCA-20829-AEHD", "55097", GuideCategory.ENTERTAINMENT),
        channel("tlchdbo", 202, "TLC Canada", "LIVE\$STARTCA-24423-TLCH", "60645", GuideCategory.ENTERTAINMENT),
        channel("flavhd", 204, "Flavour Network", "LIVE\$STARTCA-27710-FOODH", "73467", GuideCategory.ENTERTAINMENT),
        channel("ecanhd", 205, "E! Canada", "LIVE\$STARTCA-38114-E!HD", "76792", GuideCategory.ENTERTAINMENT),
        channel("fxchd", 208, "FX Canada", "LIVE\$STARTCA-29388-FXH", "73682", GuideCategory.ENTERTAINMENT),
        channel("comdy", 214, "CTV Comedy", "LIVE\$STARTCA-38135-CMDYH", "76863", GuideCategory.ENTERTAINMENT),
        channel("gametv", 218, "GameTV", "LIVE\$STARTCA-27237-GAME", "48727", GuideCategory.SPORT),
        channel("scifi", 224, "CTV Sci-Fi", "LIVE\$STARTCA-27237-GAME", "72484", GuideCategory.ENTERTAINMENT),
        channel("citsdt", 230, "YesTV", "LIVE\$STARTCA-38318-YES", "60344", GuideCategory.ENTERTAINMENT),
        channel("uvagut", 240, "Uvagut TV", "LIVE\$52520", "117039", GuideCategory.DOCUMENTARY),
        channel("homehd", 251, "Home Network", "LIVE\$STARTCA-27709-HGTV", "70676", GuideCategory.ENTERTAINMENT),
        channel("aptnhd", 253, "APTN East", "LIVE\$28750", "61998", GuideCategory.DOCUMENTARY),
        channel("drama", 254, "CTV Drama", "LIVE\$STARTCA-38130-BRVOH", "73549", GuideCategory.ENTERTAINMENT),
        channel("aptnl", 255, "APTN Languages", "LIVE\$20854", "38061", GuideCategory.DOCUMENTARY),
        channel("lifechd", 258, "Lifetime Canada", "LIVE\$STARTCA-38389-LIFEHD", "81202", GuideCategory.ENTERTAINMENT),
        channel("whd", 267, "W Network", "LIVE\$STARTCA-38122-W-HD", "71332", GuideCategory.ENTERTAINMENT),
        channel("outtv", 271, "OUTtv", "LIVE\$STARTCA-38393-OUTTV", "26776", GuideCategory.ENTERTAINMENT),
        channel("tnf", 300, "News Forum", "LIVE\$43941", "115832", GuideCategory.NEWS),
        channel("ytvhd", 305, "YTV", "LIVE\$STARTCA-36761-YTVHD", "70827", GuideCategory.KIDS),
        channel("crtn", 308, "Cartoon Network CA", "LIVE\$STARTCA-9016-TOON-E", "75456", GuideCategory.KIDS),
        channel("cpac", 381, "CPAC", "LIVE\$STARTCA-4533-CPAC", "17608", GuideCategory.DOCUMENTARY),
        channel("cpacf", 382, "CPAC French", "LIVE\$STARTCA-6622-CPACF", "21667", GuideCategory.DOCUMENTARY),
        channel("snehd", 401, "Sportsnet East", "LIVE\$STARTCA-38121-SNH-E-TORON", "62109", GuideCategory.SPORT),
        channel("snohd", 403, "Sportsnet Ontario", "LIVE\$STARTCA-33504-SNH-O-TORON", "62111", GuideCategory.SPORT),
        channel("snwhd", 405, "Sportsnet West", "LIVE\$STARTCA-27704-SNH-W", "61742", GuideCategory.SPORT),
        channel("snphd", 406, "Sportsnet Pacific", "LIVE\$STARTCA-37425-SNH-P", "61714", GuideCategory.SPORT),
        channel("sn1hd", 407, "Sportsnet One", "LIVE\$STARTCA-37441-SN1", "68859", GuideCategory.SPORT),
        channel("tsnhd", 431, "TSN1", "LIVE\$STARTCA-19430-TSN1H", "34313", GuideCategory.SPORT),
        channel("tsn2hd", 433, "TSN2", "LIVE\$STARTCA-27705-TSN2H-TORON", "61474", GuideCategory.SPORT),
        channel("tsn3hd", 435, "TSN3", "LIVE\$STARTCA-38089-TSN3H", "90119", GuideCategory.SPORT),
        channel("tsn4hd", 437, "TSN4", "LIVE\$STARTCA-38090-TSN4H-TORON", "90123", GuideCategory.SPORT),
        channel("tsn5hd", 439, "TSN5", "LIVE\$STARTCA-38091-TSN5H-TORON", "90125", GuideCategory.SPORT),
        channel("game2", 445, "GAME+", "LIVE\$STARTCA-32838-FNTSYHD", "26198", GuideCategory.SPORT),
        channel("wld", 501, "CTV Wild", "LIVE\$STARTCA-36762-ANMHD", "74041", GuideCategory.DOCUMENTARY),
        channel("usanet", 507, "USA Network CA", "LIVE\$STARTCA-37438-DISCH", "72638", GuideCategory.ENTERTAINMENT),
        channel("histhdc", 523, "History Canada", "LIVE\$STARTCA-27708-HISHD", "65619", GuideCategory.DOCUMENTARY),
        channel("bbcf", 535, "BBC First", "LIVE\$STARTCA-38383-HIFI", "53120", GuideCategory.DOCUMENTARY),
        channel("vision", 554, "VisionTV", "LIVE\$STARTCA-4546-VISION", "11220", GuideCategory.DOCUMENTARY),
        channel("amcchd", 601, "AMC Canada", "LIVE\$STARTCA-38561-AMCHD", "64831", GuideCategory.ENTERTAINMENT),
        channel("cmt", 802, "CMT Canada", "LIVE\$STARTCA-38112-CMT", "15072", GuideCategory.ENTERTAINMENT),
        channel("muchhd", 807, "Much", "LIVE\$STARTCA-38125-MUCHH", "72139", GuideCategory.ENTERTAINMENT),
    )

    val byXmlTvId: Map<String, CatalogChannel> = channels.associateBy { it.xmlTvId }
    val byId: Map<String, CatalogChannel> = channels.associateBy { it.id }
}

