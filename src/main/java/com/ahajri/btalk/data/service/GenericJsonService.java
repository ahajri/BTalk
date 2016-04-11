package com.ahajri.btalk.data.service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.beanutils.DynaBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.data.domain.json.JsonAction;
import com.ahajri.btalk.utils.ActionResultName;

import net.sf.ezmorph.bean.MorphDynaBean;

@Service("generciJsonService")
public class GenericJsonService {
	/**
	 * Persist document
	 * 
	 * @param jsonAction:
	 *            {@link JsonAction}
	 * @return {@link Boolean} while action was successful or not
	 */
	public ActionResult createDocument(JsonAction action) {
		ActionResult result = new ActionResult();
		result.setJsonReturnData("{\"error\":\" :o Ooooops, Could not create Document\"}");
		result.setActionResultName(ActionResultName.FAIL);
		result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		// Convert JSON to XML
		MorphDynaBean data = (MorphDynaBean) action.getData();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(MorphDynaBean.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			JAXBElement<MorphDynaBean> rootElement = new JAXBElement<MorphDynaBean>(new QName("discussion"),
					MorphDynaBean.class, (MorphDynaBean) data);
			m.marshal(rootElement, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		// xmlFormOfBean = sos.toString();

		// insert XML in Database

		// get result

		return result;
	}

}
