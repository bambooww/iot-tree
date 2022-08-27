/*
/*=============================================================================|
|  PROJECT Moka7                                                         1.0.2 |
|==============================================================================|
|  Copyright (C) 2013, 2016 Davide Nardella                                    |
|  All rights reserved.                                                        |
|==============================================================================|
|  SNAP7 is free software: you can redistribute it and/or modify               |
|  it under the terms of the Lesser GNU General Public License as published by |
|  the Free Software Foundation, either version 3 of the License, or under     |
|  EPL Eclipse Public License 1.0.                                             |
|                                                                              |
|  This means that you have to chose in advance which take before you import   |
|  the library into your project.                                              |
|                                                                              |
|  SNAP7 is distributed in the hope that it will be useful,                    |
|  but WITHOUT ANY WARRANTY; without even the implied warranty of              |
|  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you    |
|  decide to adopt.                                                            |
|                                                                              |
|=============================================================================*/
package Moka7Demo;

/**
 *
 * @author Dave Nardella
 */
import Moka7.*;

import java.io.*;
import java.text.*;
import java.util.Date;

import org.iottree.core.util.Convert;

public class ClientDemo {

	// If MakeAllTests = true, also DBWrite and Run/Stop tests will be performed
    private static final boolean MakeAllTests = true;
   
    private static long Elapsed;
    private static byte[] Buffer = new byte[65536]; // 64K buffer (maximum for S7400 systems)
    private static final S7Client Client = new S7Client();
    private static int ok=0;
    private static int ko=0;    
    private static String IpAddress = "";
    private static int Rack = 0; // Default 0 for S7300
    private static int Slot = 2; // Default 2 for S7300 
    private static int DBSample = 200; // Sample DB that must be present in the CPU
    private static int DataToMove; // Data size to read/write
    private static int CurrentStatus = S7.S7CpuStatusUnknown;
    
    public static void HexDump(byte[] Buffer, int Size)
    {
        int r=0;
        String Hex = "";
        
        for (int i=0; i<Size; i++)
        {
            int v = (Buffer[i] & 0x0FF);
            String hv = Integer.toHexString(v);     
            
            if (hv.length()==1)
                hv="0"+hv+" ";
            else
                hv=hv+" ";
            
            Hex=Hex+hv;
            
            r++;
            if (r==16)
            {
                System.out.print(Hex+" ");
                System.out.println(S7.GetPrintableStringAt(Buffer, i-15, 16));
                Hex="";
                r=0;
            }
        }
        int L=Hex.length();
        if (L>0)
        {
            while (Hex.length()<49)
                Hex=Hex+" ";
            System.out.print(Hex);
            System.out.println(S7.GetPrintableStringAt(Buffer, Size-r, r));                       
        }
        else
            System.out.println();
    }
    
    static void TestBegin(String FunctionName)
    {
        System.out.println();
        System.out.println("+================================================================");
        System.out.println("| "+FunctionName);
        System.out.println("+================================================================");
        Elapsed = System.currentTimeMillis();
    }
    
    static void TestEnd(int Result)
    {
    	if (Result!=0)
    	{
    		ko++;
    		Error(Result);
    	}
    	else
    		ok++;
    	System.out.println("Execution time "+(System.currentTimeMillis()-Elapsed)+" ms");
    }
       
    static void Error(int Code)
    {
        System.out.println(S7Client.ErrorText(Code));        
    }
    
    static void BlockInfo(int BlockType, int BlockNumber)
    {
        S7BlockInfo Block = new S7BlockInfo();
        TestBegin("GetAgBlockInfo()");       
        
        int Result = Client.GetAgBlockInfo(BlockType, BlockNumber, Block);
        if (Result==0)
        {
            System.out.println("Block Flags     : "+Integer.toBinaryString(Block.BlkFlags()));
            System.out.println("Block Number    : "+Block.BlkNumber());
            System.out.println("Block Languege  : "+Block.BlkLang());
            System.out.println("Load Size       : "+Block.LoadSize());
            System.out.println("SBB Length      : "+Block.SBBLength());
            System.out.println("Local Data      : "+Block.LocalData());
            System.out.println("MC7 Size        : "+Block.MC7Size());
            System.out.println("Author          : "+Block.Author());
            System.out.println("Family          : "+Block.Family());
            System.out.println("Header          : "+Block.Header());
            System.out.println("Version         : "+Block.Version());
            System.out.println("Checksum        : 0x"+Integer.toHexString(Block.Checksum()));
            SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
            System.out.println("Code Date       : "+ft.format(Block.CodeDate()));
            System.out.println("Interface Date  : "+ft.format(Block.IntfDate()));
        }
        TestEnd(Result);
    }
    
    public static boolean DBGet()
    {
        IntByRef SizeRead = new IntByRef(4);
    	TestBegin("DBGet()");
        int Result = Client.DBGet(DBSample, Buffer, SizeRead);
        TestEnd(Result);        
        if (Result==0)
        {
        	DataToMove = SizeRead.Value; // Stores DB size for next test
        	System.out.println("DB "+DBSample+" - Size read "+DataToMove+" bytes");
        	HexDump(Buffer, DataToMove);
        	return true;
        }        
        return false;        
    }
    
    public static void DBRead()
    {
    	TestBegin("ReadArea()");
    	int Result = Client.ReadArea(S7.S7AreaDB, DBSample, 0, DataToMove, Buffer);
        if (Result==0)
        {
        	System.out.println("DB "+DBSample+" succesfully read using size reported by DBGet()");
        }
        TestEnd(Result);
    }      
    
    public static void DBWrite()
    {
    	TestBegin("WriteArea()");
    	int Result = Client.WriteArea(S7.S7AreaDB, DBSample, 0, DataToMove, Buffer);
        if (Result==0)
        {
        	System.out.println("DB "+DBSample+" succesfully written using size reported by DBGet()");
        }
    	TestEnd(Result);
    }      
    /**
     * Performs read and write on a given DB
     */
    public static void DBPlay()
    {
    	// We use DBSample (default = DB 1) as DB Number
    	// modify it if it doesn't exists into the CPU.
    	if (DBGet())
    	{
    		DBRead();
    		if (MakeAllTests)
    			DBWrite();
    	}
    }
        
    public static void Delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException e) {}                    
    }
    
    public static void ShowStatus()
    {
        IntByRef PlcStatus = new IntByRef(S7.S7CpuStatusUnknown);
        TestBegin("GetPlcStatus()");       
        int Result = Client.GetPlcStatus(PlcStatus);
        if (Result==0)
        {
            System.out.print("PLC Status : ");
            switch (PlcStatus.Value)
            {
                case S7.S7CpuStatusRun :
                    System.out.println("RUN");
                    break;
                case S7.S7CpuStatusStop :
                    System.out.println("STOP");
                    break;
                default :    
                    System.out.println("Unknown ("+PlcStatus.Value+")");
            }
        }
        CurrentStatus = PlcStatus.Value;
        TestEnd(Result);
    }

    public static void DoRun()
    {
        TestBegin("PlcHotStart()");
        int Result = Client.PlcHotStart();
        if (Result==0)
        	System.out.println("PLC Started");
        TestEnd(Result);    	
    }

    public static void DoStop()
    {
        TestBegin("PlcStop()");
        int Result = Client.PlcStop();
        if (Result==0)
        	System.out.println("PLC Stopped");
        TestEnd(Result);    	    	
    }
    
    public static void RunStop()
    {
        switch (CurrentStatus)
        {
        	case S7.S7CpuStatusRun :
        		DoStop();
        		Delay(1000);
        		DoRun();
        		break;
        	case S7.S7CpuStatusStop :	
        		DoRun();
        		Delay(1000);
        		DoStop();
        }       
    }
    
    public static void GetSysInfo()
    {
        int Result;
        TestBegin("GetOrderCode()");
        S7OrderCode OrderCode = new S7OrderCode();
        Result = Client.GetOrderCode(OrderCode);
        if (Result==0)
        {
            System.out.println("Order Code        : "+OrderCode.Code());
            System.out.println("Firmware version  : "+OrderCode.V1+"."+OrderCode.V2+"."+OrderCode.V3);
        }
        TestEnd(Result);
        
        TestBegin("GetCpuInfo()");
        S7CpuInfo CpuInfo = new S7CpuInfo();
        Result = Client.GetCpuInfo(CpuInfo);
        if (Result==0)
        {
            System.out.println("Module Type Name  : "+CpuInfo.ModuleTypeName());
            System.out.println("Serial Number     : "+CpuInfo.SerialNumber());
            System.out.println("AS Name           : "+CpuInfo.ASName());
            System.out.println("CopyRight         : "+CpuInfo.Copyright());
            System.out.println("Module Name       : "+CpuInfo.ModuleName());
        }
        TestEnd(Result);

        TestBegin("GetCpInfo()");
        S7CpInfo CpInfo = new S7CpInfo();
        Result = Client.GetCpInfo(CpInfo);
        if (Result==0)
        {
            System.out.println("Max PDU Length    : "+CpInfo.MaxPduLength);
            System.out.println("Max connections   : "+CpInfo.MaxConnections);
            System.out.println("Max MPI rate (bps): "+CpInfo.MaxMpiRate);
            System.out.println("Max Bus rate (bps): "+CpInfo.MaxBusRate);
        }
        TestEnd(Result);
    }
    
    public static void GetDateAndTime()
    {
        Date PlcDateTime = new Date();
        TestBegin("GetPlcDateTime()");
        int Result = Client.GetPlcDateTime(PlcDateTime);
        if (Result==0)
            System.out.println("CPU Date/Time : "+PlcDateTime);
        TestEnd(Result);
    }    

    public static void SyncDateAndTime()
    {
        TestBegin("SetPlcSystemDateTime()");
        int Result = Client.SetPlcSystemDateTime();
        TestEnd(Result);
    }    
    
    public static void ReadSzl()
    {
        S7Szl SZL = new S7Szl(1024);
        TestBegin("ReadSZL() - ID : 0x0011, IDX : 0x0000");
        int Result = Client.ReadSZL(0x0011, 0x0000, SZL);
        if (Result==0)
        {
            System.out.println("LENTHDR : "+SZL.LENTHDR);
            System.out.println("N_DR    : "+SZL.N_DR);
            System.out.println("Size    : "+SZL.DataSize);                
            HexDump(SZL.Data,SZL.DataSize); 
        }
        TestEnd(Result);
    }
    
    public static void GetProtectionScheme()
    {
        S7Protection Protection = new S7Protection();
        TestBegin("GetProtection()");
        int Result = Client.GetProtection(Protection);
        if (Result==0)
        {
            System.out.println("sch_schal : "+Protection.sch_schal);
            System.out.println("sch_par   : "+Protection.sch_par);
            System.out.println("sch_rel   : "+Protection.sch_rel);
            System.out.println("bart_sch  : "+Protection.bart_sch);
            System.out.println("anl_sch   : "+Protection.anl_sch);
        }
        TestEnd(Result);
    }
        
    public static void Summary()
    {
    	System.out.println();
        System.out.println("+================================================================");
    	System.out.println("Tests performed : "+(ok+ko));
    	System.out.println("Passed          : "+ok);
    	System.out.println("Failed          : "+ko);    	
        System.out.println("+================================================================");
    }
    
    public static boolean Connect()
    {
    	TestBegin("ConnectTo()");
    	Client.SetConnectionType(S7.OP);
    	int Result = Client.ConnectTo(IpAddress, Rack, Slot);
    	if (Result==0)
    	{
            System.out.println("Connected to   : " + IpAddress + " (Rack=" + Rack + ", Slot=" + Slot+ ")");
            System.out.println("PDU negotiated : " + Client.PDULength()+" bytes");
    	}
    	TestEnd(Result);
    	return Result == 0;
    }
    
    
    public static void PerformTests()
    {
        GetSysInfo();
        GetProtectionScheme();
        GetDateAndTime();
        if (MakeAllTests)
        	SyncDateAndTime();
        ReadSzl();
        ShowStatus();
        if (MakeAllTests)
        	RunStop();
        BlockInfo(S7.Block_SFC,1); // Get SFC 1 info (always present in a CPU)
        DBPlay();
        Summary();
    }
    
    public static void Usage()
    {
        System.out.println("Usage");
        System.out.println("  client <IP> [Rack=0 Slot=2]");
        System.out.println("Example");
        System.out.println("  client 192.168.1.101 0 2");
        System.out.println("or");
        System.out.println("  client 192.168.1.101");    	
    }
    
    private static void my_test()
    {
    	byte[] bs = new byte[4];
    	int res = Client.ReadArea(S7.S7AreaDB, 200, 0, 4, bs);
    	TestEnd(res);
    	System.out.println("read data=="+Convert.byteArray2HexStr(bs)) ;
    	
    	bs[3] = 10 ;
    	res = Client.WriteArea(S7.S7AreaDB, 200, 0, 4, bs) ;
    	TestEnd(res);
    	System.out.println("write data=="+(res==0)) ;
    	
    	byte[] ibs = new byte[1] ;
    	res = Client.ReadArea(S7.S7AreaPE, 0,0, 1, ibs) ;
    	TestEnd(res);
    	System.out.println("read pe=="+Convert.byteArray2HexStr(ibs)) ;
    	
    	
    	//Client.set
    }
    
    public static void main(String[] args) throws IOException 
    {
    	if ((args.length!=1) && (args.length!=3))
    	{
    		Usage();
    		return;
    	}
    	if (args.length==3)
    	{
    		Rack = Integer.valueOf(args[1]);
    		Slot = Integer.valueOf(args[2]);
    	}
    	IpAddress = args[0];
    	
    	if (Connect())
    	{
            //PerformTests();
            
            my_test();
    	}
    }
    
}
