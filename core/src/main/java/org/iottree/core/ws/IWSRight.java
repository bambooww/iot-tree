package org.iottree.core.ws;

import jakarta.servlet.http.HttpSession;

public interface IWSRight
{
	public boolean checkWSRight(HttpSession session) ;
}
