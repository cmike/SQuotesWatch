package com.muustwatch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpDloader {
	public String Download (String urlString) {
		InputStream in = null;
		byte[] data = null;
		URLConnection conn = null;
		try
		{
			URL url = new URL(urlString);
			conn = url.openConnection();

			if ((conn instanceof HttpURLConnection))
			{
				HttpURLConnection httpConn = (HttpURLConnection) conn;
				httpConn.setAllowUserInteraction(false);
				httpConn.setInstanceFollowRedirects(true);
				httpConn.setRequestMethod("GET");
				httpConn.connect();

				if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) 
				{
					in = httpConn.getInputStream();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int c;
					while((c = in.read()) > -1)
						baos.write(c);
					data = baos.toByteArray();
					baos.close();
					in.close();
					String str = new String(data);
					System.out.println(str);
					return str;
				}
				else
				{                
					return("Error reading data");
				}
			}
		}
		catch (Exception ex)
		{
			return("Error in connection");
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn = null;
				}
				if (in != null)
				{
					in.close();
					in = null;
				}

			}catch(IOException ex)
			{
				return("Error: "+ex.getMessage());
			}
		}   
		return null;

	}
}
