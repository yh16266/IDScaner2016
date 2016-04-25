/*
 * 文件名：StringUtil.java
 * 包含类名列表
 * 版本信息：
 * 创建日期：2013年12月9日
 * 版权声明
 */
package com.haozi.idscaner2016.common.utils;

import android.text.Html;
import android.text.Spanned;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haozi.idscaner2016.common.base.BaseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 类名：StringUtil
 * @author yinhao
 * 实现的主要功能:提供对 String 进行处理的各类公用接口
 * 创建日期：2013年12月9日
 * [修改者，修改日期，修改内容]
 */
public class StringUtil extends BaseObject {
	
	private static Gson gson = null;

    public synchronized static Gson getGSON() {
        if (gson == null) {
        	gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss SSS").create();
        }
        return gson;
    }

    /***
     * 转换JSON字符串
     * @Title toJson
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return getGSON().toJson(obj);
    }
    
    /**
     * java对象转换JSONObject
     * @param obj
     * @return
     * @throws JSONException
     */
    public static JSONObject toJsonObject(Object obj) throws JSONException{
    	return new JSONObject(toJson(obj));
    }

    /**
     * java对象转换JSONObject
     * @param json
     * @return
     * @throws JSONException
     */
    public static JSONObject toJsonObject(String json) throws JSONException{
    	return new JSONObject(json);
    }
    
    /***
     * 转换JSON字符串
     * @Title toJson
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
			return getGSON().fromJson(json, classOfT);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return null;
    }

    /***
     * 转换JSON字符串
     * @Title toJson
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static <T> T fromJson(String json, Type tpye) {
    	try {
    		return getGSON().fromJson(json, tpye);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    
    /***
     * 判断空串
     * 
     * @Title isEmpty
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        return false;
    }

    /***
     * 判断空串
     * 
     * @Title isEmpty
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str)) {
            return true;
        }
        return false;
    }
    
    /***
     * 取长度
     * @Title length
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return 
     */
    public static int length(CharSequence str){
        if(str == null){
            return 0;
        } else {
            return str.length();
        }
    }
    
    /***
     * 判断相等（参数为null 为不等）
     * @Title equals
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static boolean equals(CharSequence s, CharSequence s1){
        if(s != null && s1 != null){
            return s.equals(s1);
        }
        return false;
    }
    
    /***
     * 判断含小写
     * @Title hasLowerChar
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static boolean hasLowerChar(CharSequence str){
        if(isEmpty(str)){
            return false;
        }
        for(char c : str.toString().toCharArray()){
            if(c >= 'a' && c <= 'z'){
                return true;
            }
        }
        return false;
    }

    /***
     * 判断含大写
     * @Title hasLowerChar
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static boolean hasUpperChar(CharSequence str){
    	if(isEmpty(str)){
    		return false;
    	}
    	for(char c : str.toString().toCharArray()){
    		if(c >= 'A' && c <= 'Z'){
    			return true;
    		}
    	}
    	return false;
    }

    /***
     * 判断含数字
     * @Title hasLowerChar
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static boolean hasNumberChar(CharSequence str){
    	if(isEmpty(str)){
    		return false;
    	}
    	for(char c : str.toString().toCharArray()){
    		if(c >= '0' && c <= '9'){
    			return true;
    		}
    	}
    	return false;
    }

	/**
	 * 判断是否为非负浮点数（正浮点数 + 0）
	 * */
	public static boolean isUpFloat(String str){
		boolean rst = false;
		if(isEmpty(str)==true){
			return rst;
		}
		//匹配非负浮点数（正浮点数 + 0）
		Pattern pattern = Pattern.compile("^\\d+(\\.\\d+)?$");
		rst = pattern.matcher(str).matches();
		return rst;
	}
    
    /***
     * 判断含非数字非字母字符
     * @Title hasLowerChar
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static boolean hasOtherChar(CharSequence str){
    	if(isEmpty(str)){
    		return false;
    	}
    	for(char c : str.toString().toCharArray()){
    		if(Character.isLowerCase(c)==false && Character.isUpperCase(c)==false 
    				&& (c >= '0' && c <= '9')==false){
    			return true;
    		}
    	}
    	return false;
    }
    
    /***
     * 判断相等（参数都为null 为等）
     * @Title strEquals
     * @Description TODO(这里用一句话描述这个方法的作用)
     * @param obj
     * @return
     */
    public static boolean strEquals(String str1, String str2) {
		if(isEmpty(str1) && isEmpty(str2)){
			return true;
		} else if(str1 != null && str1.equals(str2)){
			return true;
		} 
		return false;
	}
    
    public static Spanned formatStringColor(String orgStr, String color) {
    	String str = "<font color=\'" + color + "\'>" + orgStr + "</font>";
    	return Html.fromHtml(str);
    }
    
    public static Spanned formatStringColor(String orgStr1, String color1, String orgStr2, String color2) {
    	String str = "<font color=\'" + color1 + "\'>" + orgStr1 + "</font>" + "<font color=\'" + color2 + "\'>" + orgStr2 + "</font>";
    	return Html.fromHtml(str);
    }
    
    public static Spanned formatStringColor(String orgStr1, String color1, String orgStr2, String color2, String orgStr3, String color3) {
    	String str = "<font color=\'" + color1 + "\'>" + orgStr1 + "</font>"
    				+ "<font color=\'" + color2 + "\'>" + orgStr2 + "</font>"
    				+ "<font color=\'" + color3 + "\'>" + orgStr3 + "</font>";
    	return Html.fromHtml(str);
    }
    
    public static Spanned formatStringColor(String orgStr1, String color1, String orgStr2, String color2, String orgStr3, String color3, String orgStr4, String color4) {
    	String str = "<font color=\'" + color1 + "\'>" + orgStr1 + "</font>"
    				+ "<font color=\'" + color2 + "\'>" + orgStr2 + "</font>"
    				+ "<font color=\'" + color3 + "\'>" + orgStr3 + "</font>"
    	    		+ "<font color=\'" + color4 + "\'>" + orgStr4 + "</font>";
    	return Html.fromHtml(str);
    }
    
    public static String  escape (String src) {
    	if(src == null) return null;
        int i; 
        char j; 
        StringBuffer tmp = new StringBuffer(); 
        tmp.ensureCapacity(src.length()*6); 
        for (i=0;i< src.length() ;i++ )  { 
          j = src.charAt(i); 
          if (Character.isDigit(j) || Character.isLowerCase(j)
                || Character.isUpperCase(j)) 
              tmp.append(j); 
          else    if (j< 256)    { 
            tmp.append( "%" );
            if (j< 16) 
              tmp.append( "0" );
            tmp.append( Integer.toString(j,16) ); 
          }else{ 
            tmp.append( "%u" );
            tmp.append( Integer.toString(j,16) ); 
          }
        } 
       return tmp.toString();
    }
    
    public static String  unescape (String src) {
    	if(src == null) return null;
        StringBuffer tmp = new StringBuffer(); 
        tmp.ensureCapacity(src.length());
        int  lastPos=0,pos=0; 
        char ch; 
        while (lastPos< src.length())  { 
            pos = src.indexOf("%",lastPos); 
            if (pos == lastPos){ 
              if (src.charAt(pos+1)=='u'){ 
                ch = (char)Integer.parseInt(src.substring(pos+2,pos+6),16); 
                tmp.append(ch); 
                lastPos = pos+6;  
              }else{ 
                ch = (char)Integer.parseInt(src.substring(pos+1,pos+3),16); 
                tmp.append(ch);
                lastPos = pos+3; 
              }
            }else{ 
                  if (pos == -1){
                      tmp.append(src.substring(lastPos));
                      lastPos=src.length(); 
                   }else{     
                      tmp.append(src.substring(lastPos,pos)); 
                      lastPos=pos;
                   } 
            }
        } 
        return tmp.toString();
	}
	
	public  static String[] getDistinct(String num[]) {
         List<String> list = new java.util.ArrayList<String>();
         for (int i = 0; i < num.length; i++) {
             if (!list.contains(num[i])) {
                 list.add(num[i]); 
             }
         }
         return list.toArray(new String[0]);
	}
	
	public static String getArrayToString(String num[],String f){
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i < num.length ; i++){
			if( i != 0){
				sb.append(f);
			}
			sb.append(num[i]);
		}
		return sb.toString();
	}
	
	public static String Html2Text(String inputString) { 
	      String htmlStr = inputString; //含html标签的字符串 
	      String textStr =""; 
	      Pattern p_script;
	      java.util.regex.Matcher m_script; 
	      Pattern p_style;
	      java.util.regex.Matcher m_style; 
	      Pattern p_html;
	      java.util.regex.Matcher m_html; 
	      
	      Pattern p_html1;
	      java.util.regex.Matcher m_html1; 
	   
	      try { 
	       String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; //定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script> } 
	       String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; //定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style> } 
	          String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式 
	          String regEx_html1 = "<[^>]+"; 
	          p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
	          m_script = p_script.matcher(htmlStr); 
	          htmlStr = m_script.replaceAll(""); //过滤script标签 

	          p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
	          m_style = p_style.matcher(htmlStr); 
	          htmlStr = m_style.replaceAll(""); //过滤style标签 
	      
	          p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
	          m_html = p_html.matcher(htmlStr); 
	          htmlStr = m_html.replaceAll(""); //过滤html标签 
	          
	          p_html1 = Pattern.compile(regEx_html1,Pattern.CASE_INSENSITIVE); 
	          m_html1 = p_html1.matcher(htmlStr); 
	          htmlStr = m_html1.replaceAll(""); //过滤html标签 
	      
	          
	       textStr = htmlStr; 
	      
	      }catch(Exception e) { 
	               System.err.println("Html2Text: " + e.getMessage()); 
	      } 
	   
	      return textStr;//返回文本字符串 
	} 

	/**
	 * 判断是否为数字
	 * */
	public static boolean isNumeric(String str){
		boolean rst = false;
		if(isEmpty(str)==true){
			return rst;
		}
	    Pattern pattern = Pattern.compile("[0-9]*");
	    rst = pattern.matcher(str).matches();
		return rst;
	}

	/**
	 * 判断是否为手机号码
	 * */
	public static boolean isMobile(String str){
		boolean rst = false;
		if(isEmpty(str)==true){
			return rst;
		}
		//String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";  
		String regExp = "^[1][0-9]{10}$";  
		Pattern pattern = Pattern.compile(regExp);
		rst = pattern.matcher(str).matches();
		return rst;
	}
	
	/**加密手机号码*/
	public static String encryptPhone(String strPhone) {
		if(!StringUtil.isEmpty(strPhone) && strPhone.length() >= 4){
			if(strPhone.length() > 8){
				strPhone = strPhone.substring(0, strPhone.length()-8) 
						+"*****"+strPhone.substring(strPhone.length()-3, strPhone.length());
			}else{
				strPhone = "*****"+strPhone.substring(strPhone.length()-3, strPhone.length());
			}
		}
		
		return strPhone;
	}
	
	public static String sqliteEscape(String keyWord) {
		keyWord = keyWord.replace("/", "//");
		keyWord = keyWord.replace("'", "''");
		keyWord = keyWord.replace("[", "/[");
		keyWord = keyWord.replace("]", "/]");
		keyWord = keyWord.replace("%", "/%");
		keyWord = keyWord.replace("&", "/&");
		keyWord = keyWord.replace("_", "/_");
		keyWord = keyWord.replace("(", "/(");
		keyWord = keyWord.replace(")", "/)");
		return keyWord;
	}
	
    public static final byte[] compress(String paramString) { 
        if (paramString == null){
        	return null; 
        }
        ByteArrayOutputStream byteArrayOutputStream = null; 
        ZipOutputStream zipOutputStream = null; 
        byte[] arrayOfByte; 
        try { 
            byteArrayOutputStream = new ByteArrayOutputStream(); 
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream); 
            zipOutputStream.putNextEntry(new ZipEntry("0")); 
            zipOutputStream.write(paramString.getBytes()); 
            zipOutputStream.closeEntry(); 
            arrayOfByte = byteArrayOutputStream.toByteArray(); 
        } catch (IOException localIOException5) { 
            arrayOfByte = null; 
        } finally { 
            if (zipOutputStream != null) 
                try { 
                    zipOutputStream.close(); 
                } catch (IOException localIOException6) { 
            } 
            if (byteArrayOutputStream != null) 
                try { 
                    byteArrayOutputStream.close(); 
                } catch (IOException localIOException7) { 
            } 
        } 
        return arrayOfByte; 
    } 
	 
    public static final String decompress(byte[] paramArrayOfByte) { 
        if (paramArrayOfByte == null) 
            return null; 
        ByteArrayOutputStream byteArrayOutputStream = null; 
        ByteArrayInputStream byteArrayInputStream = null; 
        ZipInputStream zipInputStream = null; 
        String str; 
        try { 
            byteArrayOutputStream = new ByteArrayOutputStream(); 
            byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte); 
            zipInputStream = new ZipInputStream(byteArrayInputStream); 
            //ZipEntry localZipEntry = zipInputStream.getNextEntry(); 
            byte[] arrayOfByte = new byte[1024]; 
            int i = -1; 
            while ((i = zipInputStream.read(arrayOfByte)) != -1) 
                byteArrayOutputStream.write(arrayOfByte, 0, i); 
            str = byteArrayOutputStream.toString(); 
        } catch (IOException localIOException7) { 
            str = null; 
        } finally { 
            if (zipInputStream != null) 
                try { 
                    zipInputStream.close(); 
                } catch (IOException localIOException8) { 
                } 
            if (byteArrayInputStream != null) 
                try { 
                    byteArrayInputStream.close(); 
                } catch (IOException localIOException9) { 
                } 
            if (byteArrayOutputStream != null) 
                try { 
                    byteArrayOutputStream.close(); 
                } catch (IOException localIOException10) { 
            } 
        } 
        return str; 
    }

}
