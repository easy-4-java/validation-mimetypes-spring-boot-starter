package io.github.easy4j.validation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("rawtypes")
public abstract class FiletypeUtils {

	protected static final String MIMETYPES_PROPERTIES = "fileTypes.properties";
	protected static Properties mFileTypes;
	
	static{
		try {
			mFileTypes = new Properties();
			mFileTypes.load(FilemimeUtils.class.getResourceAsStream(MIMETYPES_PROPERTIES));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getFileType(File file) {
		if (file == null) {
			return null;
		}
        if(!file.exists() || file.length()<11){
            return null;
        }
        String header = get10ByteHeader(file);
        String fileSuffix = mFileTypes.getProperty(header);
		/* 
		 * 优化处理：在不同的设备上同样类型的文件，文件头前面内容未必一致，可能只有前几个一致，后面就不同了
		 * （例如：jpg类型文件，在不同手机上，lennovo k900前10个是一致的，但是MI3只有前5个字符一致，后面是不一样的，所有一些情况进行特殊处理）当整个头文件失败后，
		 * 在进行前5个字符截取对比处理，优化具体如下：
		 */
        if(StringUtils.isEmpty(fileSuffix)){
        	
			Iterator keyList = mFileTypes.keySet().iterator();
        	//并不是所有的文件格式前10 byte（jpg）都一致，前五个byte一致即可
            String key,keySearchPrefix = header.substring(0,5);
            while (keyList.hasNext()){
                key = (String) keyList.next();
                if(key.contains(keySearchPrefix)) {
                    fileSuffix = mFileTypes.getProperty(key);
                    break;
                }
            }
        }
        
        //前5个字符截取对比处理没有找到，则进行特殊处理
        if(StringUtils.isEmpty(fileSuffix)){
        	header = get3ByteHeader(file);
        	fileSuffix = mFileTypes.getProperty(header);
        }
		
		return fileSuffix;
	}

    private static byte[] inputStreamToFirstBytes(InputStream is) throws IOException {
        int extent = 11;
        int cur = 0;
        byte[] ret = new byte[extent];

        is.mark(extent); // throws IOException if not supported

        while (cur < extent) {
            int n = is.read(ret, cur, extent - cur);
            if (n == -1) {
                // EOF; return a correctly-sized Array
                is.reset();
                return Arrays.copyOf(ret, cur);
            } else {
                cur += n;
            }
        }

        is.reset();

        return ret;
    }

    public static String getFileType(InputStream is) throws IOException {
        Callable<byte[]> getBytes = new Callable<byte[]>() {
            @Override
            public byte[] call() throws IOException {
                return inputStreamToFirstBytes(is);
            }
        };

        byte[] bytes;
        try {
            bytes = getBytes.call();
        } catch (Exception e) {
            throw new IOException(e);
        }
        if (bytes == null || bytes.length < 11) {
            return null;
        }
		
		String header = bytesToHexString(ArrayUtils.subarray(bytes, 0, 10));
		String fileSuffix = mFileTypes.getProperty(header);
		/* 
		 * 优化处理：在不同的设备上同样类型的文件，文件头前面内容未必一致，可能只有前几个一致，后面就不同了
		 * （例如：jpg类型文件，在不同手机上，lennovo k900前10个是一致的，但是MI3只有前5个字符一致，后面是不一样的，所有一些情况进行特殊处理）当整个头文件失败后，
		 * 在进行前5个字符截取对比处理，优化具体如下：
		 */
        if(StringUtils.isEmpty(fileSuffix)){
        	Iterator keyList = mFileTypes.keySet().iterator();
        	//并不是所有的文件格式前10 byte（jpg）都一致，前五个byte一致即可
            String key,keySearchPrefix = header.substring(0,5);
            while (keyList.hasNext()){
                key = (String) keyList.next();
                if(key.contains(keySearchPrefix)) {
                    fileSuffix = mFileTypes.getProperty(key);
                    break;
                }
            }
        }
        
        //前5个字符截取对比处理没有找到，则进行特殊处理
        if(StringUtils.isEmpty(fileSuffix)){
        	header = bytesToHexString(ArrayUtils.subarray(bytes, 0, 3));
        	fileSuffix = mFileTypes.getProperty(header);
        }
		
		return fileSuffix;
		
	}
	
	@SuppressWarnings("deprecation")
	private static String get10ByteHeader(File file) {
        InputStream input = null;
        String value = null;
        try {
            input = new FileInputStream(file);
            byte[] b = new byte[10];
            input.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
        	IOUtils.closeQuietly(input);
        }
        return value;
    }
	
	@SuppressWarnings("deprecation")
	private static String get3ByteHeader(File file) {
        InputStream input = null;
        String value = null;
        try {
            input = new FileInputStream(file);
            byte[] b = new byte[3];
            input.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
        	IOUtils.closeQuietly(input);
        }
        return value;
    }

    private static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    
}
