package smartx.businesslogic;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMessageConverter {

		private static JsonMessageConverter jsonMessageConverterService;
		private static ObjectMapper jsonConverter;
        
		public ObjectMapper getJsonConverter() {
			
			return jsonConverter;
		}

		public JsonMessageConverter() {
			jsonConverter = new ObjectMapper();
			jsonConverter.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			jsonConverter.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
			jsonConverter.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
		}
		
		public static JsonMessageConverter getInstance()
		{
			if(jsonMessageConverterService == null)
			{
				jsonMessageConverterService = new JsonMessageConverter();
			}
			return jsonMessageConverterService;
		}

}