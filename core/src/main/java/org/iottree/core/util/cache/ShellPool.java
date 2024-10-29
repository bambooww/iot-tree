package org.iottree.core.util.cache;

import java.io.* ;
import java.util.* ;

public class ShellPool
{
	 private Vector avail , busy ;

	 public ShellPool ()
	 {
		  avail = new Vector () ;
		  busy = new Vector () ;
	 }

	 public ShellPool (int initnum)
	 {
		  this () ;
		  for (int i = 0 ; i < initnum ; i ++)
			   avail.addElement (new Shell()) ;
	 }

	 //public abstract
	 public synchronized Shell getShell ()
	 {
		  if (!avail.isEmpty())
		  {
			   Shell exShell = (Shell)avail.lastElement () ;
			   int lastIndex = avail.size() - 1 ;
			   avail.removeElementAt (lastIndex) ;

			   busy.addElement (exShell) ;
			   return (exShell) ;
		  }
		  else
		  {
			   //if (total < max)
			   Shell newShell = new Shell () ;
			   busy.addElement (newShell) ;
			   return newShell ;
		  }
	 }

	 public synchronized void freeShell (Shell sh)
	 {
		  sh.clear () ;

		  busy.removeElement (sh) ;
		  avail.addElement (sh) ;
	 }
	 /*
	 public static void main (String[] args)
	 {
		  ShellPool sp = new ShellPool (10000) ;
		  int i ;
		  long s,e ;
		  Shell tmpsh ;
		  s = System.currentTimeMillis () ;
		  for (i = 0 ; i < 10000 ; i ++)
			   tmpsh = new Shell () ;
		  e = System.currentTimeMillis () ;

		  System.out.println ("Not use shell pool cost="+(e-s)) ;
		  //=0
		  s = System.currentTimeMillis () ;

		  for (i = 0 ; i < 10000 ; i ++)
			   tmpsh = sp.getShell () ;
		  e = System.currentTimeMillis () ;
		  System.out.println ("Using shell pool cost="+(e-s)) ;
		  //=20
	 }*/
}