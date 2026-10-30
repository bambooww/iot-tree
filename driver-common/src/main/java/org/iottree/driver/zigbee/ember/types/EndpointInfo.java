package org.iottree.driver.zigbee.ember.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.driver.zigbee.zcl.ZCLClusterIDS;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;

public class EndpointInfo
{
	/** Actual Zigbee endpoint number. uint8_t */
	public int endpoint;
	/** Profile ID of the device on this endpoint. */
	public int profileId;
	/** Device ID of the device on this endpoint. uint16_t */
	public int deviceId;
	/** Version of the device. uint8_t */
	public int deviceVersion;
	/** List of server clusters. */
	public List<Integer> inClusterList;
	/** List of client clusters. */
	public List<Integer> outClusterList;
	/** Network index for this endpoint. uint8_t */
	public int networkIndex;
	/** Multicast group IDs to register in the multicast table */
	public List<Integer> multicastIds;

	EndpointInfo(int ep, int prof_id, int dev_id, int dev_ver, List<Integer> in_clusters, List<Integer> out_clusters,
			int nwk_idx, List<Integer> multi_idxs)
	{
		this.endpoint = ep;
		this.profileId = prof_id;
		this.deviceId = dev_id;
		this.deviceVersion = dev_ver;
		this.inClusterList = in_clusters;
		this.outClusterList = out_clusters;
		this.networkIndex = nwk_idx;
		this.multicastIds = multi_idxs;
	}

	public static final ArrayList<EndpointInfo> FIXED_ENDPOINTS = new ArrayList<>();
	static
	{
		FIXED_ENDPOINTS.add(new EndpointInfo(1, ZSpecConsts.HA_PROFILE_ID, 0x65, 1,
				Arrays.asList(ZCLClusterIDS.genBasic, // 0x0000,// Basic
						ZCLClusterIDS.genIdentify, // 0x0003,// Identify
						ZCLClusterIDS.genOnOff, // 0x0006,// On/off
						ZCLClusterIDS.genLevelCtrl, // 0x0008,// Level Control
						ZCLClusterIDS.genTime, // 0x000A,// Time
						ZCLClusterIDS.genOta, // 0x0019,// Over the Air
												// Bootloading
						// Cluster.genPowerProfile.ID,// 0x001A,// Power Profile
						// XXX: missing ZCL cluster def in Z2M?
						ZCLClusterIDS.lightingColorCtrl),
				Arrays.asList(ZCLClusterIDS.genBasic, // 0x0000,// Basic
						ZCLClusterIDS.genIdentify, // 0x0003,// Identify
						ZCLClusterIDS.genGroups, // 0x0004,// Groups
						ZCLClusterIDS.genScenes, // 0x0005,// Scenes
						ZCLClusterIDS.genOnOff, // 0x0006,// On/off
						ZCLClusterIDS.genLevelCtrl, // 0x0008,// Level Control
						ZCLClusterIDS.genPollCtrl, // 0x0020,// Poll Control
						ZCLClusterIDS.lightingColorCtrl, // 0x0300,// Color
															// Control
						ZCLClusterIDS.msIlluminanceMeasurement, // 0x0400,//
																// Illuminance
																// Measurement
						ZCLClusterIDS.msTemperatureMeasurement, // 0x0402,//
																// Temperature
																// Measurement
						ZCLClusterIDS.msRelativeHumidity, // 0x0405,// Relative
															// Humidity
															// Measurement
						ZCLClusterIDS.msOccupancySensing, // 0x0406,// Occupancy
															// Sensing
						ZCLClusterIDS.ssIasZone, // 0x0500,// IAS Zone
						ZCLClusterIDS.seMetering, // 0x0702,// Simple Metering
						ZCLClusterIDS.seMeterIdentification, // 0x0B01,// Meter
																// Identification
						ZCLClusterIDS.haApplianceStatistics, // 0x0B03,//
																// Appliance
																// Statistics
						ZCLClusterIDS.haElectricalMeasurement, // 0x0B04,//
																// Electrical
																// Measurement
						ZCLClusterIDS.touchlink),
				0x00, Arrays.asList(0, 901)));

		FIXED_ENDPOINTS.add(new EndpointInfo(ZSpecConsts.GP_ENDPOINT, ZSpecConsts.GP_PROFILE_ID, 0x66, 1,
				Arrays.asList(ZCLClusterIDS.greenPower // 0x0021,// Green Power
				), Arrays.asList(ZCLClusterIDS.greenPower // 0x0021,// Green
															// Power
				), 0x00, Arrays.asList(0x0b84)));
	}

	public static EndpointInfo findEndpointInfo(int end_point)
	{
		for (EndpointInfo epi : FIXED_ENDPOINTS)
		{
			if (epi.endpoint == end_point)
			{
				return epi;
			}
		}
		return null;
	}
}
