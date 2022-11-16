package cn.ac.iscas.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iscas.datasong.lib.common.DataSongException;
import com.iscas.datasong.lib.common.Status;
import com.iscas.datasong.lib.util.DataSongExceptionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by ZQM on 2016/5/24.
 */
public class DataSongJsonUtils {
	private static TimeZone timeZone;
	private static volatile ObjectMapper mapper;
	private static volatile ObjectMapper includeNullMapper;

	/**
	 * 对象转json
	 *
	 * @param object
	 * @return
	 */
	public static String toJson(Object object) {
		try {
			return getMapper().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toFormatJson(Object object) {
		return formatJson(toJson(object));
	}

	/**
	 * 对象转json
	 *
	 * @param object
	 * @return
	 */
	public static String toJsonIncludeNull(Object object) {
		try {
			return getMapper(true).writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T fromJson(String json, Class<T> classOfT) throws DataSongException {
		//				return gson.fromJson(json, classOfT);
		try {
			return getMapper().readValue(json, classOfT);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataSongException(Status.PARAM_ERROR, String.format("analyze json error: %s", DataSongExceptionUtils.getExceptionInfo(e)));
		}
	}

	public static <T> T fromJson(String json, JavaType javaType) throws DataSongException {
		//				return gson.fromJson(json, classOfT);
		try {
			return getMapper().readValue(json, javaType);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataSongException(Status.PARAM_ERROR, String.format("analyze json error: %s", DataSongExceptionUtils.getExceptionInfo(e)));
		}
	}

	/**
	 * @param json
	 * @param typeReference
	 * @param <T>           new TypeReference<HashMap<String,Field>>() {}
	 * @return
	 */
	public static <T> T fromJson(String json, TypeReference typeReference) throws DataSongException {
		try {
			return (T) getMapper().readValue(json, typeReference);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataSongException(Status.PARAM_ERROR,
					String.format("invalidate json data formt [%s]\n%s", json, DataSongExceptionUtils.getExceptionInfo(e)));
		}
	}

	/**
	 * 适用于泛型形式的转换
	 *
	 * @param json
	 * @param clz
	 * @param clzParameter
	 * @param <T>
	 * @return
	 * @throws DataSongException
	 */
	public static <T> T fromJson(String json, Class clz, Class... clzParameter) throws DataSongException {
		try {
			JavaType javaType = getMapper().getTypeFactory().constructParametricType(clz, clzParameter);
			return getMapper().readValue(json, javaType);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DataSongException(Status.PARAM_ERROR,
					String.format("invalidate json data formt [%s]\n%s", json, DataSongExceptionUtils.getExceptionInfo(e)));
		}

	}

	private static ObjectMapper getMapper() {
		return getMapper(false);
	}

	private static ObjectMapper getMapper(boolean includeNull) {
		if (includeNull) {
			if (includeNullMapper == null) {
				synchronized (DataSongJsonUtils.class) {
					if (includeNullMapper == null) {
						includeNullMapper = new ObjectMapper();
			/*ObjectMapper configure = mapper
				.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
					true);*/
						//为null的不输出
//				includeNullMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
						//大小写问题
						includeNullMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

						//设置等同于@JsonIgnoreProperties(ignoreUnknown = true)
						includeNullMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
						includeNullMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);//防止转为json是首字母大写的属性会出现两次
						includeNullMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

						//设置JSON时间格式
						SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						includeNullMapper.setDateFormat(myDateFormat);
						if (timeZone != null) {
							includeNullMapper.setTimeZone(timeZone);
						}

//			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE CLOSE_CLOSEABLE)
					}
				}

			}
			return includeNullMapper;
		} else {
			if (mapper == null) {
				synchronized (DataSongJsonUtils.class) {
					if (mapper == null) {
						mapper = new ObjectMapper();
			/*ObjectMapper configure = mapper
				.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
					true);*/
						//为null的不输出
						mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
						//大小写问题
						mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

						//设置等同于@JsonIgnoreProperties(ignoreUnknown = true)
						mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
						mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);//防止转为json是首字母大写的属性会出现两次
						mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

						//设置JSON时间格式
						SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						mapper.setDateFormat(myDateFormat);
						if (timeZone != null) {
							mapper.setTimeZone(timeZone);
						}

//			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE CLOSE_CLOSEABLE)
					}
				}
			}
			return mapper;
		}

	}

	/**
	 * 单位缩进字符串。
	 */
	private static String SPACE = "\t";

	/**
	 * 返回格式化JSON字符串。
	 *
	 * @param json 未格式化的JSON字符串。
	 * @return 格式化的JSON字符串。
	 */
	public static String formatJson(String json) {
		StringBuffer result = new StringBuffer();

		int length = json.length();
		int number = 0;
		char key = 0;

		//遍历输入字符串。
		for (int i = 0; i < length; i++) {
			//1、获取当前字符。
			key = json.charAt(i);

			//2、如果当前字符是前方括号、前花括号做如下处理：
			if ((key == '[') || (key == '{')) {
				//（1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
				if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
					result.append('\n');
					result.append(indent(number));
				}

				//（2）打印：当前字符。
				result.append(key);

				//（3）前方括号、前花括号，的后面必须换行。打印：换行。
				result.append('\n');

				//（4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
				number++;
				result.append(indent(number));

				//（5）进行下一次循环。
				continue;
			}

			//3、如果当前字符是后方括号、后花括号做如下处理：
			if ((key == ']') || (key == '}')) {
				//（1）后方括号、后花括号，的前面必须换行。打印：换行。
				if (!result.toString().endsWith("\n")) {
					result.append('\n');
				}

				//（2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
				number--;
				result.append(indent(number));

				//（3）打印：当前字符。
				result.append(key);

				//（4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
				if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
					result.append('\n');
				}

				//（5）继续下一次循环。
				continue;
			}

			//4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
			if ((key == ',')) {
				result.append(key);
				result.append('\n');
				result.append(indent(number));
				continue;
			}

			//5、打印：当前字符。
			result.append(key);
		}

		return result.toString();
	}

	/**
	 * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
	 *
	 * @param number 缩进次数。
	 * @return 指定缩进次数的字符串。
	 */
	private static String indent(int number) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < number; i++) {
			result.append(SPACE);
		}
		return result.toString();
	}

	public static synchronized void setTimeZone(TimeZone timeZone) {
		DataSongJsonUtils.timeZone = timeZone;
	}

	public static synchronized void updateMapperTimeZone() {
		if (mapper != null) {
			mapper.setTimeZone(timeZone);
		}
		if (includeNullMapper != null) {
			includeNullMapper.setTimeZone(timeZone);
		}
	}
}
