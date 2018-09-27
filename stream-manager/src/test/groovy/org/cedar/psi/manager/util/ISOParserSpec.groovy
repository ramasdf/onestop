package org.cedar.psi.manager.util

import groovy.json.JsonOutput
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ISOParserSpec extends Specification {

  def "Identifier info is correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def idInfo = ISOParser.parseIdentifierInfo(document)

    then:
    idInfo.fileId == 'gov.super.important:FILE-ID'
    idInfo.doi == 'doi:10.5072/FK2TEST'
    idInfo.parentId == 'gov.super.important:PARENT-ID'
  }

  def "Citation info is correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def citationInfo = ISOParser.parseCitationInfo(document)

    then:
    citationInfo.fileIdentifier == 'gov.super.important:FILE-ID'
    citationInfo.parentIdentifier == 'gov.super.important:PARENT-ID'
    citationInfo.hierarchyLevelName == 'granule'
    citationInfo.doi == 'doi:10.5072/FK2TEST'
    citationInfo.purpose == 'Provide quality super important data to the user community.'
    citationInfo.status == 'completed'
    citationInfo.credit == null
    citationInfo.title == 'Important Organization\'s Important File\'s Super Important Title'
    citationInfo.alternateTitle == 'Still (But Slightly Less) Important Alternate Title'
    citationInfo.description == 'Wall of overly detailed, super informative, extra important text.'
    citationInfo.thumbnail == 'https://www.example.com/exportImage?soCool=yes&format=png'
    citationInfo.thumbnailDescription == 'Preview graphic'
    citationInfo.creationDate == null
    citationInfo.revisionDate == '2011-01-02'
    citationInfo.publicationDate == '2010-11-15'
    citationInfo.citeAsStatements == ['[CITE AS STATEMENT 1]', '[CITE AS STATEMENT 2]'] as Set
    citationInfo.crossReferences == [
        [
            title: '[TITLE OF PUBLICATION]',
            date: '9999-01-01',
            links: [[
                        linkName: null,
                        linkProtocol: null,
                        linkUrl: 'HTTPS://WWW.EXAMPLE.COM',
                        linkDescription: '[DESCRIPTION OF URL]',
                        linkFunction: 'information'
                    ]]
        ]
    ] as Set

    citationInfo.largerWorks == [
        [
            title: '[TITLE OF PROJECT]',
            date: '9999-10-10',
            links: []
        ]
    ] as Set

    citationInfo.useLimitation == '[NOAA LEGAL STATEMENT]'
    citationInfo.legalConstraints == ['[CITE AS STATEMENT 1]', '[CITE AS STATEMENT 2]'] as Set
    citationInfo.accessFeeStatement == 'template fees'
    citationInfo.orderingInstructions == 'template ordering instructions'
    citationInfo.edition == '[EDITION]'
  }

  def "Keywords and topics are correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def parsedXml = ISOParser.parseKeywordsAndTopics(document)

    then:
    // Deep equality check
    JsonOutput.toJson(parsedXml.keywords) == JsonOutput.toJson([
        [
            "values": [
                "SIO > Super Important Organization",
                "OSIO > OTHER SUPER IMPORTANT ORGANIZATION",
                "SSIO > Super SIO (Super Important Organization)"
            ],
            "type": "dataCenter",
            "namespace": "GCMD Keywords - Data Centers"
        ],
        [
            "values": [
                "0038924",
                "0038947",
                "0038970"
            ],
            "type": null,
            "namespace": "NCEI ACCESSION NUMBER"
        ],
        [
            "values": [
                "EARTH SCIENCE SERVICES > ENVIRONMENTAL ADVISORIES > FIRE ADVISORIES > WILDFIRES",
                "EARTH SCIENCE > This Keyword is > Misplaced and Invalid",
                "This Keyword > Is Just > WRONG"
            ],
            "type": "service",
            "namespace": "Global Change Master Directory Science and Services Keywords"
        ],
        [
            "values": [
                "Air temperature",
                "Water temperature"
            ],
            "type": "theme",
            "namespace": "Miscellaneous keyword type"
        ],
        [
            "values": [
                "Wind speed",
                "Wind direction"
            ],
            "type": "theme",
            "namespace": "Miscellaneous keyword type"
        ],
        [
            "values": [
                "EARTH SCIENCE > ATMOSPHERE > ATMOSPHERIC TEMPERATURE > SURFACE TEMPERATURE > DEW POINT TEMPERATURE",
                "EARTH SCIENCE > OCEANS > SALINITY/DENSITY > SALINITY",
                "EARTH SCIENCE > VOLCANOES > THIS KEYWORD > IS INVALID",
                "Earth Science > Spectral/Engineering > microwave > Brightness Temperature",
                "Earth Science > Spectral/Engineering > microwave > Temperature Anomalies"
            ],
            "type": "theme",
            "namespace": "GCMD Keywords - Science Keywords"
        ],
        [
            "values": [
                "GEOGRAPHIC REGION > ARCTIC",
                "OCEAN > ATLANTIC OCEAN > NORTH ATLANTIC OCEAN > GULF OF MEXICO",
                "LIQUID EARTH > THIS KEYWORD > IS INVALID"
            ],
            "type": "place",
            "namespace": "GCMD Keywords - Locations"
        ],
        [
            "values": [
                "SEASONAL"
            ],
            "type": "dataResolution",
            "namespace": "Global Change Master Directory Keywords - Temporal Data Resolution"
        ],
        [
            "values": [
                "> 1 km"
            ],
            "type": "dataResolution",
            "namespace": "GCMD Keywords - Vertical Data Resolution"
        ]
    ] as Set)
    parsedXml.topicCategories == ['environment', 'oceans'] as Set

  }

  def "Temporal bounding is correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def temporalBounding = ISOParser.parseTemporalBounding(document)

    then:
    temporalBounding == [
        beginDate           : '2005-05-09T00:00:00Z',
        beginIndeterminate  : null,
        beginYear           : 2005,
        endDate             : '2010-10-01',
        endIndeterminate    : null,
        endYear             : 2010,
        instant             : null,
        instantIndeterminate: null,
        description         : null
    ]
  }

  def "Very old temporal bounding is correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-paleo-dates-metadata.xml").text

    when:
    def temporalBounding = ISOParser.parseTemporalBounding(document)

    then:
    temporalBounding == [
        beginDate           : null,
        beginIndeterminate  : null,
        beginYear           : -617905000,
        endDate             : '-1601050',
        endIndeterminate    : null,
        endYear             : -1601050,
        instant             : null,
        instantIndeterminate: null,
        description         : 'Start_Date: 6181000 cal yr BP; Stop_Date: 1603000 cal yr BP; '
    ]
  }

  def "Temporal bounding without time zone information is correctly parsed with UTC"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-no-timezone-dates-metadata.xml").text

    when:
    def temporalBounding = ISOParser.parseTemporalBounding(document)

    then:
    temporalBounding == [
        beginDate           : '2005-05-09T00:00:00Z',
        beginIndeterminate  : null,
        beginYear           : 2005,
        endDate             : '2010-10-01T00:00:00Z',
        endIndeterminate    : null,
        endYear             : 2010,
        instant             : null,
        instantIndeterminate: null,
        description         : null
    ]
  }

  def "Invalid temporal bounding creates invalidDates entry"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-invalid-dates-metadata.xml").text

    when:
    def temporalBounding = ISOParser.parseTemporalBounding(document)

    then:
    temporalBounding == [
        beginDate           : '1984-04-31',
        beginIndeterminate  : null,
        beginYear           : null,
        endDate             : '1985-05-09T00:00:00Z',
        endIndeterminate    : null,
        endYear             : 1985,
        instant             : null,
        instantIndeterminate: null,
        description         : null,
        invalidDates        : [
          begin  : true,
          end    : false,
          instant: false
        ]
    ]
  }

  def "Polygon spatial bounding is correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def result = ISOParser.parseSpatialInfo(document)

    then:
    result == [
        spatialBounding: [
            type       : 'Polygon',
            coordinates: [
                [[-180, -90], [180, -90], [180, 90], [-180, 90], [-180, -90]]
            ]
        ],
        isGlobal       : true
    ]
  }

  def "Point spatial bounding is correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-point-cords-metadata.xml").text

    when:
    def spatialBounding = ISOParser.parseSpatialInfo(document)

    then:
    spatialBounding == [
        spatialBounding: [
            type       : 'Point',
            coordinates: [-105, 40]
        ],
        isGlobal       : false
    ]
  }

  def "Null Spatial bounding is correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-null-cords-metadata.xml").text

    when:
    def spatialBounding = ISOParser.parseSpatialInfo(document)

    then:
    spatialBounding == [spatialBounding: null, isGlobal: false]
  }

  def "LineString spatial bounding is prevented"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-linestring-coords-metadata.xml").text

    when:
    def spatialBounding = ISOParser.parseSpatialInfo(document)

    then:
    spatialBounding == [
        spatialBounding: [
            type       : 'LineString',
            coordinates: [[-80, -10],[80, -10]]
        ],
        isGlobal       : false
    ]
  }

  def "Acquisition info is correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def parsedXml = ISOParser.parseAcquisitionInfo(document)

    then:
    parsedXml.acquisitionInstruments == [
        [
            instrumentIdentifier : 'SII > Super Important Instrument',
            instrumentType       : 'sensor',
            instrumentDescription: 'The Super Important Organization\'s (SIO) Super Important Instrument (SII) is a really impressive sensor designed to provide really important information from the TumbleSat system.'
        ]
    ] as Set
    parsedXml.acquisitionOperations == [
        [
            operationDescription: null,
            operationIdentifier : 'Super Important Project',
            operationStatus     : null,
            operationType       : null
        ]
    ] as Set
    parsedXml.acquisitionPlatforms == [
        [
            platformIdentifier : 'TS-18 > TumbleSat-18',
            platformDescription: 'The TumbleSat satellite system offers the advantage of daily surprise coverage, with morning and afternoon orbits that collect and deliver data in every direction. The information received includes brief glimpses of earth, other satellites, and the universe beyond, as the system spirals out of control.',
            platformSponsor    : ['Super Important Organization', 'Other (Kind Of) Important Organization']
        ]
    ] as Set
  }

  def "Data formats are correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def dataFormats = ISOParser.parseDataFormats(document)

    then:
    dataFormats == [
        [name: 'NETCDF', version: 'classic'],
        [name: 'NETCDF', version: '4'],
        [name: 'ASCII', version: null],
        [name: 'CSV', version: null]
    ] as Set
  }

  def "Links are correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def links = ISOParser.parseLinks(document)

    then:
    links == [[
                  linkName       : 'Super Important Access Link',
                  linkProtocol   : 'HTTP',
                  linkUrl        : 'http://www.example.com',
                  linkDescription: 'Everything Important, All In One Place',
                  linkFunction   : 'search'
              ]] as Set
  }

  def "Responsible parties are correctly parsed"() {
    // FIXME
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def responsibleParties = ISOParser.parseResponsibleParties(document)

    then:
    responsibleParties == [
        [
            individualName  : null,
            organizationName: 'Super Important Organization',
            positionName    : null,
            role            : 'publisher',
            email           : 'email@sio.co',
            phone           : '555-123-4567'
        ],
        [
            individualName  : 'John Smith',
            organizationName: 'University of Awesome',
            positionName    : 'Chief Awesomeness Officer',
            role            : 'pointOfContact',
            email           : 'john.smith@uoa.edu',
            phone           : '555-555-5555'
        ],
        [
            individualName  : 'Jane Doe',
            organizationName: 'University of Awesome',
            positionName    : 'VP of Awesome Behavior',
            role            : 'distributor',
            email           : 'jane.doe@uoa.edu',
            phone           : '555-555-5556'
        ],
        [
            individualName  : 'Jarianna Whackositz',
            organizationName: 'Secret Underground Society',
            positionName    : 'Software Developer',
            role            : 'resourceProvider',
            email           : 'jw@mock-creator-email.org',
            phone           : '555-555-5558'
        ],
        [
            individualName  : 'Dr. Quinn McClojure Man',
            organizationName: 'Soap Boxes Inc.',
            positionName    : 'Software Developer',
            role            : 'originator',
            email           : 'dqmm@mock-creator-email.org',
            phone           : '555-555-5559'
        ],
        [
            individualName  : 'Zebulon Pike',
            organizationName: 'Pikes Peak Inc.',
            positionName    : 'Software Developer',
            role            : 'principalInvestigator',
            email           : 'zp@mock-creator-email.org',
            phone           : '555-555-5560'
        ],
        [
            individualName  : 'Little Rhinoceros',
            organizationName: 'Alien Infested Spider Monkey Rescue',
            positionName    : 'Software Developer',
            role            : 'author',
            email           : 'lr@mock-creator-email.org',
            phone           : '555-555-5561'
        ],
        [
            individualName  : 'Skeletor McSkittles',
            organizationName: 'The Underworld',
            positionName    : 'Bringer of Skittles',
            role            : 'collaborator',
            email           : 'sm@mock-creator-email.org',
            phone           : '555-555-5562'
        ],
        [
            individualName: null,
            organizationName: 'Global Change Data Center, Science and Exploration Directorate, Goddard Space Flight Center (GSFC) National Aeronautics and Space Administration (NASA)',
            positionName: null,
            role: 'custodian',
            email: null,
            phone: null
        ]
    ] as Set
  }

  def "DSMM scores are correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def dsmm = ISOParser.parseDSMM(document)

    then:
    dsmm.Accessibility == 4
    dsmm.DataIntegrity == 0
    dsmm.DataQualityAssessment == 2
    dsmm.DataQualityAssurance == 3
    dsmm.DataQualityControlMonitoring == 1
    dsmm.Preservability == 5
    dsmm.ProductionSustainability == 4
    dsmm.TransparencyTraceability == 2
    dsmm.Usability == 3
    dsmm.average == ((dsmm.Accessibility + dsmm.DataIntegrity + dsmm.DataQualityAssessment + dsmm.DataQualityAssurance +
        dsmm.DataQualityControlMonitoring + dsmm.Preservability + dsmm.ProductionSustainability +
        dsmm.TransparencyTraceability + dsmm.Usability) / (dsmm.size() - 1))
  }

  def "Services are correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text
    Map expectedResult = [
        title:'Multibeam Bathymetric Surveys ArcGIS Map Service',
        alternateTitle:'Alternate Title for Testing',
        abstract: "NOAA's National Centers for Environmental Information (NCEI) is the U.S. national archive for multibeam bathymetric data and presently holds over 2400 surveys received from sources worldwide, including the U.S. academic fleet via the Rolling Deck to Repository (R2R) program. In addition to deep-water data, the multibeam database also includes hydrographic multibeam survey data from the National Ocean Service (NOS). This map service shows navigation for multibeam bathymetric surveys in NCEI's archive. Older surveys are colored orange, and more recent recent surveys are green.",
        date: '2012-01-01',
        dateType: 'creation',
        pointOfContact: [
            individualName: '[AT LEAST ONE OF ORGANISATION, INDIVIDUAL OR POSITION]',
            organizationName: '[AT LEAST ONE OF ORGANISATION, INDIVIDUAL OR POSITION]'
        ],
        operations:[
            [
                protocol:'http',
                url:'https://maps.ngdc.noaa.gov/arcgis/rest/services/web_mercator/multibeam/MapServer',
                applicationProfile:'https://www.geoplatform.gov/spec/esri-map-rest',
                name:'Multibeam Bathymetric Surveys ArcGIS Cached Map Service',
                description: 'Capabilities document for Open Geospatial Consortium Web Map Service for Multibeam Bathymetric Surveys'
            ],
            [
                protocol:'http',
                url:'https://maps.ngdc.noaa.gov/arcgis/services/web_mercator/multibeam_dynamic/MapServer/WMSServer?request=GetCapabilities&service=WMS',
                applicationProfile:'http://opengis.net/spec/wms',
                name:'Multibeam Bathymetric Surveys Web Map Service (WMS)',
                description: 'The Multibeam Bathymetric Surveys ArcGIS cached map service provides rapid display of ship tracks from global scales down to zoom level 9 (approx. 1:1,200,000 scale).'
            ]
        ]
    ]
    when:
    Set serviceTextBlobs = ISOParser.parseServices(document)

    then:
    notThrown(Exception)
    serviceTextBlobs[0] as Map == expectedResult
  }

  def "Miscellaneous items are correctly parsed"() {
    given:
    def document = ClassLoader.systemClassLoader.getResourceAsStream("test-iso-metadata.xml").text

    when:
    def miscellaneous = ISOParser.parseMiscellaneous(document)

    then:
    miscellaneous.updateFrequency == 'asNeeded'
    miscellaneous.presentationForm == 'tableDigital'
  }
}
