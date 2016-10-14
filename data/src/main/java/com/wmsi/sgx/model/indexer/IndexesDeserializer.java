package com.wmsi.sgx.model.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

/**
 * Elastic Search Index Deserializer
 * 
 */
public class IndexesDeserializer extends JsonDeserializer<Object>{

	/**
	 * Deserializes the object
	 * 
	 *@param DeserializationContext 
	 *@param JsonParser
	 * @return Deserialized Object
	 * @throws IOException
	 * @throws JsonProcessingException
	 *             
	 */
	
	@Override
	public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);

		Iterator<Entry<String, JsonNode>> i = node.fields();
		List<Index> idxs = new ArrayList<Index>();

		while(i.hasNext()){
			Entry<String, JsonNode> field = i.next();			
			JsonNode fieldNode = field.getValue();	
			
			List<String> aliases = null;
			JsonNode aliasNode = fieldNode.get("aliases");

			Index idx = new Index();
			idx.setName(field.getKey());

			if(aliasNode != null){
				aliases = Lists.newArrayList(aliasNode.fieldNames());
				
				if(aliases != null && !aliases.isEmpty())
					idx.setAliases(aliases);
			}

			idxs.add(idx);
		}

		Indexes ret = new Indexes();
		ret.setIndexes(idxs);
		
		return ret;
	}
}
