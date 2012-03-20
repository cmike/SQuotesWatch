package com.muustwatch;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ChartView extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        
				WebView webView = (WebView)findViewById(R.id.chartWebView);
				webView.getSettings().setJavaScriptEnabled(true);
				webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
				webView.clearCache(true);
				
		
				// Code below cause : "Browser does not support charting" message
//				StringBuilder html = new StringBuilder();
//				html.append("<html>");
//				html.append("  <head>");
//				html.append("   <script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>");
//				html.append("    <script type=\"text/javascript\">");
//				html.append("  google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});");
//				html.append("      google.setOnLoadCallback(drawChart);");
//				html.append("      function drawChart() {");
//				html.append("var data = new google.visualization.DataTable();");
//				html.append("data.addColumn('string', 'Year');");
//				html.append("data.addColumn('number', 'Sales');");
//				html.append("data.addColumn('number', 'Expenses');");
//				html.append("				        data.addRows(4);");
//				html.append("data.setValue(0, 0, '2004');");
//				html.append("data.setValue(0, 1, 1000);");
//				html.append("data.setValue(0, 2, 400);");
//				html.append("data.setValue(1, 0, '2005');");
//				html.append("data.setValue(1, 1, 1170);");
//				html.append("data.setValue(1, 2, 460);");
//				html.append("data.setValue(2, 0, '2006');");
//				html.append("data.setValue(2, 1, 860);");
//				html.append("data.setValue(2, 2, 580);");
//				html.append("data.setValue(3, 0, '2007');");
//				html.append("data.setValue(3, 1, 1030);");
//				html.append("data.setValue(3, 2, 540);");
//
//				html.append("var chart = new google.visualization.LineChart(document.getElementById('chart_div'));");
//				html.append("chart.draw(data, {width: 400, height: 240, title: 'Company Performance'});");
//				html.append("}");
//				html.append("</script>");
//				html.append("</head>");
//
//				html.append("<body>");
//				html.append("<div id=\"chart_div\"></div>");
//				html.append("</body>");
//				html.append("</html>");
//		
//				webView.loadData(html.toString(), "text/html", "utf-8");
				//String mUrl = "http://chart.apis.google.com/chart?cht=p3&chd=t30,60,10&chs=250x100&chl=cars|bikes|trucks";
				//String mUrl = "https://chart.googleapis.com/chart?cht=p3&chd=t:60,40&chs=250x100&chl=Hello|World";
				String mUrl = "https://chart.googleapis.com/chart?cht=p3&chd=t:30,60,10&chs=250x100&chl=cars|bikes|trucks";
				webView.loadUrl(mUrl);
	}
}
