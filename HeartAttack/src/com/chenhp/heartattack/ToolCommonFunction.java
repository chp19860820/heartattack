package com.chenhp.heartattack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;




public class ToolCommonFunction {
	
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);

		for (i = 0; i < src.length(); i++) {

			j = src.charAt(i);

			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(
							src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(
							src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}
    public static String[] com_Split(String str,char x) throws Exception
    {
        String s = new String();
        String[] arr;
        int len=1, k=0;

        for (k=0; k<str.length(); k++)
            if (str.charAt(k) == x) len++;

        arr = new String[len];
        k = 0;
        for(int i=0; i<str.length(); i++)
        {
            if(str.charAt(i) == x)
            {
                arr[k] = s;
                k++;
                s = new String();
            }
            else
            {
                s += str.charAt(i);
            }
        }
        arr[k] = s;

        return arr;
    }

    public static String com_Join(String[] arr, String x) throws Exception
    {
        String s = new String();

        for (int i=0; i<(arr.length - 1); i++)
			s += (arr[i] + x);

		s += arr[arr.length-1];

        return s;
    }
    
    public static String com_ReplaceSubstring(String src, char rep, String tar) throws Exception
    {
        String[] s = com_Split(src, rep);
        return com_Join(s, tar);
    }
    public static String com_GetSubStrBetween(String srcStr, String startStr,String endStr, boolean case_insens) throws Exception
    {
		int stPos=0;
		int enPos=0;
		String srcStr_L;
		
		srcStr_L = srcStr;
		if(!case_insens){
			srcStr_L = srcStr.toLowerCase();
			startStr = startStr.toLowerCase();
			endStr = endStr.toLowerCase();
		}
		stPos = srcStr_L.indexOf(startStr);
		if("".equals(endStr)){
			enPos = srcStr.length();
		}else{
			enPos = stPos+startStr.length()+srcStr_L.substring(stPos+startStr.length()).indexOf(endStr);
		}
		if(stPos<1){
			return "";
		}
		stPos = stPos + startStr.length();
		if(enPos < stPos){
			return srcStr.substring(stPos);
		}else{
			return srcStr.substring(stPos,enPos);
		}
    }
    /*public static void saveToSetup(String key, String value) throws IOException {
		File file = new File("C:\\net\\setup.ini");
		if (!file.exists()) {
			file.createNewFile();
		}
		saveToFile(file, key, value);
	}
    public static void saveToFile(File file, String key, String value) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file, true);
			os.write(("\r\n" + key + "=" + value).getBytes("utf-8"));
		} catch (Exception e) {
		} finally {
			try {
				os.close();
				os = null;
			} catch (Exception e) {
			}
		}
	}
    public static HashMap<String, String> getInitMap() {
		BufferedReader bufferReader = null;
		try {
			File file = new File("C:\\net\\setup.ini");
			if (!file.exists()) {
				file = new File("setup.ini");
				file.createNewFile();
			}
			// ToolLog.printInfo("filePath:" + file.getAbsolutePath());
			bufferReader = new BufferedReader(new FileReader(file));
			String line = null;
			HashMap<String, String> map = new HashMap<String, String>();
			while ((line = bufferReader.readLine()) != null) {
				try {
					// 清除注释
					if (line.indexOf("#") == 0 || line.trim().equals("")) {
						continue;
					}
					String[] param = line.split("=");
					map.put(param[0], param[1]);
				} catch (Exception e) {
					// 一般出错了不需要处理
				}
			}
			return map;
		} catch (Exception e) {
			return null;
		} finally {
			if (bufferReader != null) {
				try {
					bufferReader.close();
					bufferReader = null;
				} catch (IOException e) {
				}
			}
		}
	}*/
}
