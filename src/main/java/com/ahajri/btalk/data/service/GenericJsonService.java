package com.ahajri.btalk.data.service;

import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.data.domain.json.JsonAction;
import com.ahajri.btalk.utils.ActionResultName;

@Service("generciJsonService")
public class GenericJsonService {
	/**
	 * Persist document
	 * @param jsonAction: {@link JsonAction}
	 * @return {@link Boolean} while action was successfull or not  
	 */
	public ActionResult createDocument(JsonAction jsonAction){
		ActionResult result=new ActionResult();
		result.setCode(-1);
		result.setJsonReturnData("\"error\":\" :o Ooooops, I'm sorry MAN !!!\"}");
		result.setActionResultName(ActionResultName.FAIL);
		//Convert JSON to XML
		System.out.println("##################");
		//insert XML in Database 
		
		//get result
		
		return result;
	}

	

}
