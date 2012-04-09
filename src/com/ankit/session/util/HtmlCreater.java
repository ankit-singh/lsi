package com.ankit.session.util;

/**
 * @author ankitsingh
 *
 */
public class HtmlCreater {
	private StringBuffer sBuff;
	public HtmlCreater(String title){
		sBuff = new StringBuffer();
		sBuff.append("<html><head><title>").append(title).append("</title></head>");
		sBuff.append("<body>");
	}
	public void addHeading(String heading,int value){
		sBuff.append("<h").append(value).append(">");
		sBuff.append(heading);
		sBuff.append("</h").append(value).append(">");
	}
	public void startForm(String responseType, String actionName){
		sBuff.append("<form method=").append(responseType).append(" action=").append(actionName).append(">");
		
	}
	public void addInputText(String name,int size,int maxLength){
		sBuff.append("<input type=text name=").append(name).append(" size=").append(size).append(" maxlength=").append(maxLength);
		sBuff.append(">");
	}
	public void addSubmitButton(String name,String value){
		sBuff.append("<input type=submit name=").append(name).append(" value=").append(value).append(">");
	}
	public void endForm(){
		sBuff.append("</form>");
	}
	public void addText(String text){
		sBuff.append(text);
	}
	public String getHtml(){
		sBuff.append("</body></html>");
		return sBuff.toString();
	}
}
