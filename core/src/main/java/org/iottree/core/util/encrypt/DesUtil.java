package org.iottree.core.util.encrypt;

import java.io.UnsupportedEncodingException;

import org.iottree.core.util.Convert;


class DesUtil
{
    //IP: Array[1..64]
    static final byte[] IP = new byte[]
        {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
        };
    //InvIP: Array[1..64]
    static final byte[] InvIP = new byte[]
        {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
        };
    //E: Array[1..48]
    static final byte[] E = new byte[]
        {
            32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
        };
    //P: Array[1..32]
    static final  byte[] P = new byte[]
        {
            16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
        };
    //SBoxes: Array[1..8, 0..3, 0..15]
    static final byte[][][] SBoxes = new byte[][][]
        {
            {
                {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
            },

            {
                {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
            },

            {
                {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
            },

            {
                {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
            },

            {
                {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
            },

            {
                {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
            },

            {
                {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
            },

            {
                {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
            }
        };

      //PC_1: Array[1..56]
      static final byte[] PC_1 = new byte[]
            {
                57, 49, 41, 33, 25, 17, 9,
                1, 58, 50, 42, 34, 26, 18,
                10, 2, 59, 51, 43, 35, 27,
                19, 11, 3, 60, 52, 44, 36,
                63, 55, 47, 39, 31, 23, 15,
                7, 62, 54, 46, 38, 30, 22,
                14, 6, 61, 53, 45, 37, 29,
                21, 13, 5, 28, 20, 12, 4
            };
        //PC_2: Array[1..48]
        static final byte[] PC_2 = new byte[]
        {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
        };
        //ShiftTable: Array[1..16]
        static final byte[] ShiftTable = new byte[]
            {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};


//    Var
  //InputValue: Array[1..64] Of Byte;
  byte[] InputValue = new byte[64] ;
  //OutputValue: Array[1..64] Of Byte;
  byte[] OutputValue = new byte[64] ;
  //RoundKeys: Array[1..16, 1..48] Of Byte;
  byte[][] RoundKeys = new byte[16][48] ;
  //L, R, FunctionResult: Array[1..32] Of Byte;
  byte[] L = new byte[32] ;
  byte[] R = new byte[32] ;
  byte[] FunctionResult = new byte[32] ;
  //C, D: Array[1..28] Of Byte;
  byte[] C = new byte[28] ;
  byte[] D = new byte[28] ;

    public void  DES(byte[] Input/*64bits*/, byte[] Output/*64bits*/,byte[] Key,boolean Encrypt)
    {
        //System.out.println ("input=="+toBin(Input)) ;
        //System.out.println ("key==") ;
        //printByteArray(Key) ;
        byte n,i,b,Round ;
        byte[] Outputje = new byte[64] ;
        byte[] K = new byte[48] ;
  //  Var
  //n,i, b, Round: Byte;
  //Outputje: Array[1..64] Of Byte;
  //K: Array[1..48] Of Byte;
  //fi: Text;
  //Begin
  //For n := 1 To 64 Do InputValue[n] := GetBit(Input, n);
        for (n = 0 ; n < 64 ; n ++)
            InputValue[n] = getBit (Input,n) ;
        //System.out.println ("InputValue=="+toBin(InputValue)) ;
    //For n := 1 To 28 Do Begin
      //C[n] := GetBit(Key, PC_1[n]);
     // D[n] := GetBit(Key, PC_1[n + 28]);
    //End;
        for (n = 0 ; n < 28 ; n ++)
        {
            C[n] = getBit (Key,PC_1[n]-1) ;
            D[n] = getBit (Key,PC_1[n+28]-1) ;
        }
        //System.out.println ("C=="+toBin(C)) ;
        //System.out.println ("D=="+toBin(D)) ;
  //For n := 1 To 16 Do SubKey(n, RoundKeys[n]);
        for (n = 0 ; n < 16 ; n ++)
        {
            subkey (n,RoundKeys[n]) ;
            //System.out.println ("RoundKeys="+n+"="+toBin(RoundKeys[n])) ;
        }
  //For n := 1 To 64 Do If n <= 32 Then L[n] := InputValue[IP[n]] Else R[n - 32] := InputValue[IP[n]];
        for (n = 0 ; n < 64 ; n ++)
        {
            if (n<32)
            {
                L[n] = InputValue[IP[n]-1] ;
            }
            else
            {
                R[n - 32] = InputValue[IP[n]-1];
            }
        }

        //System.out.println ("L=="+toBin(L)) ;
        //System.out.println ("R=="+toBin(R)) ;
      /*
  For Round := 1 To 16 Do Begin
      If Encrypt Then
        F(R, RoundKeys[Round], FunctionResult)
      Else
        F(R, RoundKeys[17 - Round], FunctionResult);
      For n := 1 To 32 Do FunctionResult[n] := FunctionResult[n] Xor L[n];
      L := R;
      R := FunctionResult;
    End;*/
        for (Round = 0 ; Round < 16 ; Round ++)
        {
            if (Encrypt)
            {
                F(R, RoundKeys[Round], FunctionResult);
            }
            else
            {
                F(R, RoundKeys[15 - Round], FunctionResult);
            }

        //System.out.println ("*****R=="+toBin(R)) ;
            //System.out.println ("FunctionResult=="+toBin(FunctionResult)) ;
            for (n = 0 ; n < 32 ; n ++)
            {
                //FunctionResult[n] = (byte)(((int)FunctionResult[n]) ^ ((int)L[n]));
                FunctionResult[n] = myXor (FunctionResult[n],L[n]) ;
            }

            System.arraycopy (R,0,L,0,R.length) ;
            //L = R ;
            //R = FunctionResult ;
            System.arraycopy (FunctionResult,0,R,0,R.length) ;

            //System.out.println ("LLLL=="+toBin(L)) ;
        //System.out.println ("rrrR=="+toBin(R)) ;
        }


/*
  For n := 1 To 64 Do Begin
      b := InvIP[n];
      If b <= 32 Then OutputValue[n] := R[b] Else OutputValue[n] := L[b - 32];
    End; 这段基于0可能会有问题
    */
        for (n = 0 ; n < 64 ; n ++)
        {
            b = (byte)(InvIP[n] - 1) ;
            if (b < 32)
            {
                OutputValue[n] = R[b] ;
            }
            else
            {
                OutputValue[n] = L[b - 32] ;
            }
        }

        //System.out.println ("OutputValue=="+toBin(OutputValue)) ;
  //For n := 1 To 64 Do SetBit(Output, n, OutputValue[n]);
    //End;
        for(n = 0 ; n < 64 ; n ++)
        {
            setBit(Output, n, OutputValue[n]);
        }


    }

    /**
     * 得到输入字串中的第index位置bit中的值 1=true,0=false
     *
     */
    static byte getBit(byte[] data,int index)
    {
     	if (index >= data.length * 8)
            throw new RuntimeException ("getBit index too big than data!") ;

		int tmpb = 0x80;

		tmpb = tmpb >>> (index % 8);

		//for (int i = 0; i < data.length; i ++)
			//printByte(data[i]);

		if ((data[index/8] & tmpb)>0) return 1 ;


		return 0 ;
    }


    /**
     * 设置输入字串中的第index位置bit中的值 1=true,0=false
     *
     */


     static byte [] setBit(byte[] data, int index, byte value)
    {
        if (index>=data.length*8)
            throw new RuntimeException ("getBit index too big than data!") ;

        int tmpb = 0x80 ;
        tmpb = tmpb >>> (index % 8) ;

		if ((data[index/8] & tmpb) > 0) //如果原来的位为 1
        {
            if (value==0) //如果需要设置为 0
            {
                data[index/8] = (byte)(data[index/8] - tmpb) ;
            }
        }
        else
        {
            if (value==1) //如果需要设置为 1
            {
                data[index/8] = (byte)(data[index/8] + tmpb) ;
            }
        }
		return data;
    }

    /**
     * 对28字节数组循环左移一个字节
     */
    void shift (byte[] subKeyPart)
    {
        //Procedure Shift(Var SubKeyPart);
/*
  Var
    SKP: Array[1..28] Of Byte absolute SubKeyPart;
    n, b: Byte;
*/
        byte[] SKP = subKeyPart ;
        byte n,b ;
        /*
      Begin
        b := SKP[1];
        For n := 1 To 27 Do SKP[n] := SKP[n + 1];
        SKP[28] := b;
      End; {Shift}
      */
        b = SKP[0] ;
        for (n = 0 ; n < 27 ; n ++)
        {
            SKP[n] = SKP[n + 1];
        }
        SKP[27] = b;
    }
    /**
     * 根据ShiftTable表，对48字节
     */
    void subkey (int round,byte[] subkey)
    {
        byte[] SK = subkey ;
        byte n,b ;

        for (n = 0 ; n < ShiftTable[round] ; n ++)
        {
            shift (C) ;
            shift (D) ;
        }
        for (n = 0 ; n < 48 ; n ++)
        {
            b = (byte)(PC_2[n] - 1);
            if( b < 28)
            {
                SK[n] = C[b] ;
            }
            else
            {
                SK[n] = D[b - 28];
            }
        }
    }
    /**
     * F操作
     */
    void F(byte[] FR,byte[] FK,byte[] Output)
    {
        byte[] R = FR ;
        byte[] K = FK ;
        byte[] Temp1 = new byte[48] ;
        byte[] Temp2 = new byte[32] ;
        int n,h,i,j,Row,Column ;
        byte[] TotalOut = Output ;

  
    for(n = 0 ; n < 48 ; n ++)
    {
         Temp1[n] = myXor(R[E[n]-1],K[n]) ;
    }
    
    for (n = 0 ; n < 8 ; n ++)
    {
        i = n * 6 ;
        j = n * 4 ;
        Row = Temp1[i] * 2 + Temp1[i+6-1] ;
        Column = Temp1[i + 2-1] * 8 + Temp1[i + 3-1] * 4 + Temp1[i + 4-1] * 2 + Temp1[i + 5-1];
        for(h = 0 ; h < 4 ; h ++)
        {//System.out.println ("j="+j+" h="+h) ;
            switch (h)
            {
                case 0: Temp2[j + h] =(byte)( (SBoxes[n][Row][Column] & 8) / 8);
                    break ;
                case 1: Temp2[j + h] =(byte)( (SBoxes[n][Row][Column] & 4) / 4);
                    break ;
                case 2: Temp2[j + h] =(byte)( (SBoxes[n][Row][Column] & 2) / 2);
                    break ;
                case 3: Temp2[j + h] =(byte)( (SBoxes[n][Row][Column] & 1));
                    break ;
            }
        }
    }
        //For n := 1 To 32 Do TotalOut[n] := Temp2[P[n]];
        for(n = 0 ; n < 32 ; n ++)
        {
            TotalOut[n] = Temp2[P[n]-1]; //mb>>>TotalOut[n] = Temp2[P[n]];
        }
        //System.out.println ("TotalOut=="+toBin(TotalOut)) ;
        //System.out.println (">>>>Output=="+toBin(Output)) ;
      //End; {F}
    }

    /**
	* 将一个字节的内容打印出来
	*/
	static void printByte(byte data)
	{
		int n = 0x80;

		for (int i = 0; i < 8; i++)
		{
			if ( (n & data) == 0)
				System.out.print("0");
			else
				System.out.print("1");

			n = n >>> 1;
		}
		System.out.print(" ");
	}

	/**
	* 将一个字节数组中的内容打印出来
	*/
	static void printByteArray(byte[] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			printByte(data[i]);
		}
	}

    static String toHex(byte b)
    {
        int i = (int)b ;
        i = 0x000000FF&i ;
        String s = Integer.toHexString(i).toUpperCase() ;
        if(s.length()==1)
          return "0"+s ;
        return s ;
    }

    static String toBin(byte[] bs)
    {
        StringBuffer tmpsb = new StringBuffer (bs.length) ;
        for (int i = 0 ; i < bs.length ; i ++)
            if (i%8==7)
                tmpsb.append (bs[i]+" ") ;
            else
                tmpsb.append (bs[i]) ;
        return tmpsb.toString () ;
    }

    static byte myXor (byte a,byte b)
    {
        if (a==b)
            return 0 ;
        else
            return 1 ;
    }
    
    
    static short[] pc1_permtab ={ 
	         8,  7,                                         /* 64 bit -> 56 bit*/
	        57, 49, 41, 33, 25, 17,  9,
	         1, 58, 50, 42, 34, 26, 18,
	        10,  2, 59, 51, 43, 35, 27,
	        19, 11,  3, 60, 52, 44, 36,
	        63, 55, 47, 39, 31, 23, 15,
	         7, 62, 54, 46, 38, 30, 22,
	        14,  6, 61, 53, 45, 37, 29,
	        21, 13,  5, 28, 20, 12,  4
	};
    
    static void permute(short[] ptable, byte[] in, byte[] out)
	{
		//System.out.println("in len=="+in.length);
		short ib, ob; /* in-bytes and out-bytes */
		short bb, b_bit; /* counter for bit and byte */
		ib = ptable[0];// pgm_read_byte(&(ptable[0]));
		ob = ptable[1];// pgm_read_byte(&(ptable[1]));
		// ptable = &(ptable[2]);
		for (bb = 0; bb < ob; ++bb)
		{
			short x, t = 0;
			for (b_bit = 0; b_bit < 8; ++b_bit)
			{
				x = (short) (ptable[bb * 8 + b_bit + 2] - 1);// pgm_read_byte(&(ptable[byte*8+b_bit]))
																// -1 ;
				t <<= 1;
				if (((in[x / 8]) & (0x80 >> (x % 8))) > 0)
				{
					t |= 0x01;
				}
			}
			out[bb] = (byte) t;
		}
	}
    
    public static void main (String[] args) throws UnsupportedEncodingException
    {
        DesUtil du = new DesUtil () ;
        byte[] input = "tybB5678".getBytes("UTF-8") ;
        System.out.println("input len="+input.length) ;
        //for(int i = 3 ; i < 8 ; i ++)
        //  input[i] = '\0';
        byte[] output = new byte[8] ;
        byte[] key = "Th347678".getBytes("UTF-8") ;
        //byte[] key_out = new byte[8] ;
        //permute(pc1_permtab, key, key_out) ;
        du.DES(input,output,key,true) ;
        
        System.out.println ("enc="+Convert.byteArray2HexStr(output).toUpperCase()) ;
        byte[] output2 = new byte[8] ;
        du.DES(output,output2,key,false) ;
        
        System.out.println ("dec="+new String(output2)) ;
    }
}

