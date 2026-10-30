package org.iottree.driver.zigbee.zcl;

public class ZCLClusterIDS
{
	public static final int genBasic = 0x0000 ;

	public static final int genPowerCfg = 0x0001 ;

	public static final int genDeviceTempCfg = 0x0002 ;

	public static final int genIdentify = 0x0003 ;

	public static final int genGroups = 0x0004 ;

	public static final int genScenes = 0x0005 ;

	public static final int genOnOff = 0x0006 ;

	public static final int genOnOffSwitchCfg = 0x0007 ;

	public static final int genLevelCtrl = 0x0008 ;

	public static final int genAlarms = 0x0009 ;

	public static final int genTime = 0x000A ;

	public static final int genRssiLocation = 0x000B ;

	public static final int genAnalogInput = 0x000C ;

	public static final int genAnalogOutput = 0x000D ;

	public static final int genAnalogValue = 0x000E ;

	public static final int genBinaryInput = 0x000F ;

	public static final int genBinaryOutput = 0x0010 ;

	public static final int genBinaryValue = 0x0011 ;

	public static final int genMultistateInput = 0x0012 ;

	public static final int genMultistateOutput = 0x0013 ;

	public static final int genMultistateValue = 0x0014 ;

	public static final int genCommissioning = 0x0015 ;

	public static final int piPartition = 0x0016 ;

	public static final int genOta = 0x0019 ;

	public static final int powerProfile = 0x001A ;

	public static final int haApplianceControl = 0x001B ;

	public static final int pulseWidthModulation = 0x001C ;

	public static final int genPollCtrl = 0x0020 ;

	public static final int greenPower = 0x0021 ;

	public static final int mobileDeviceCfg = 0x0022 ;

	public static final int neighborCleaning = 0x0023 ;

	public static final int nearestGateway = 0x0024 ;

	public static final int keepAlive = 0x0025 ;

	public static final int zigbeeDirectConfiguration = 0x003D ;

	public static final int closuresShadeCfg = 0x0100 ;

	public static final int closuresDoorLock = 0x0101 ;

	public static final int closuresWindowCovering = 0x0102 ;

	public static final int barrierControl = 0x0103 ;

	public static final int hvacPumpCfgCtrl = 0x0200 ;

	public static final int hvacThermostat = 0x0201 ;

	public static final int hvacFanCtrl = 0x0202 ;

	public static final int hvacDehumidificationCtrl = 0x0203 ;

	public static final int hvacUserInterfaceCfg = 0x0204 ;

	public static final int lightingColorCtrl = 0x0300 ;

	public static final int lightingBallastCfg = 0x0301 ;

	public static final int msIlluminanceMeasurement = 0x0400 ;

	public static final int msIlluminanceLevelSensing = 0x0401 ;

	public static final int msTemperatureMeasurement = 0x0402 ;

	public static final int msPressureMeasurement = 0x0403 ;

	public static final int msFlowMeasurement = 0x0404 ;

	public static final int msRelativeHumidity = 0x0405 ;

	public static final int msOccupancySensing = 0x0406 ;

	public static final int msLeafWetness = 0x0407 ;

	public static final int msSoilMoisture = 0x0408 ;

	public static final int pHMeasurement = 0x0409 ;

	public static final int msElectricalConductivity = 0x040A ;

	public static final int msWindSpeed = 0x040B ;

	public static final int msCarbonMonoxide = 0x040C ;

	public static final int msCO2 = 0x040D ;

	public static final int msEthylene = 0x040E ;

	public static final int msEthyleneOxide = 0x040F ;

	public static final int msHydrogen = 0x0410 ;

	public static final int msHydrogenSulfide = 0x0411 ;

	public static final int msNitricOxide = 0x0412 ;

	public static final int msNitrogenDioxide = 0x0413 ;

	public static final int msOxygen = 0x0414 ;

	public static final int msOzone = 0x0415 ;

	public static final int msSulfurDioxide = 0x0416 ;

	public static final int msDissolvedOxygen = 0x0417 ;

	public static final int msBromate = 0x0418 ;

	public static final int msChloramines = 0x0419 ;

	public static final int msChlorine = 0x041A ;

	public static final int msFecalColiformAndEColi = 0x041B ;

	public static final int msFluoride = 0x041C ;

	public static final int msHaloaceticAcids = 0x041D ;

	public static final int msTotalTrihalomethanes = 0x041E ;

	public static final int msTotalColiformBacteria = 0x041F ;

	public static final int msTurbidity = 0x0420 ;

	public static final int msCopper = 0x0421 ;

	public static final int msLead = 0x0422 ;

	public static final int msManganese = 0x0423 ;

	public static final int msSulfate = 0x0424 ;

	public static final int msBromodichloromethane = 0x0425 ;

	public static final int msBromoform = 0x0426 ;

	public static final int msChlorodibromomethane = 0x0427 ;

	public static final int msChloroform = 0x0428 ;

	public static final int msSodium = 0x0429 ;

	public static final int pm25Measurement = 0x042A ;

	public static final int msFormaldehyde = 0x042B ;

	public static final int ssIasZone = 0x0500 ;

	public static final int ssIasAce = 0x0501 ;

	public static final int ssIasWd = 0x0502 ;

	public static final int piGenericTunnel = 0x0600 ;

	public static final int piBacnetProtocolTunnel = 0x0601 ;

	public static final int piAnalogInputReg = 0x0602 ;

	public static final int piAnalogInputExt = 0x0603 ;

	public static final int piAnalogOutputReg = 0x0604 ;

	public static final int piAnalogOutputExt = 0x0605 ;

	public static final int piAnalogValueReg = 0x0606 ;

	public static final int piAnalogValueExt = 0x0607 ;

	public static final int piBinaryInputReg = 0x0608 ;

	public static final int piBinaryInputExt = 0x0609 ;

	public static final int piBinaryOutputReg = 0x060A ;

	public static final int piBinaryOutputExt = 0x060B ;

	public static final int piBinaryValueReg = 0x060C ;

	public static final int piBinaryValueExt = 0x060D ;

	public static final int piMultistateInputReg = 0x060E ;

	public static final int piMultistateInputExt = 0x060F ;

	public static final int piMultistateOutputReg = 0x0610 ;

	public static final int piMultistateOutputExt = 0x0611 ;

	public static final int piMultistateValueReg = 0x0612 ;

	public static final int piMultistateValueExt = 0x0613 ;

	public static final int pi11073ProtocolTunnel = 0x0614 ;

	public static final int piIso7818ProtocolTunnel = 0x0615 ;

	public static final int retailTunnel = 0x0617 ;

	public static final int seMetering = 0x0702 ;

	public static final int seTunneling = 0x0704 ;

	public static final int telecommunicationsInformation = 0x0900 ;

	public static final int telecommunicationsVoiceOverZigbee = 0x0904 ;

	public static final int telecommunicationsChatting = 0x0905 ;

	public static final int haApplianceIdentification = 0x0B00 ;

	public static final int seMeterIdentification = 0x0B01 ;

	public static final int haApplianceEventsAlerts = 0x0B02 ;

	public static final int haApplianceStatistics = 0x0B03 ;

	public static final int haElectricalMeasurement = 0x0B04 ;

	public static final int haDiagnostic = 0x0B05 ;

	public static final int touchlink = 0x1000 ;

	public static final int manuSpecificTuya = 0xEF00 ;

	public static final int manuSpecificAmazonWWAH = 0xFC57 ;


}
