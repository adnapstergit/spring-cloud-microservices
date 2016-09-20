/**
 * (C) Copyright 2016 Johnson Controls, Inc
 * Use or Copying of all or any part of this program, except as
 * permitted by License Agreement, is prohibited.
 */
package com.jci.item.service;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jci.item.azure.data.DataHelper;
import com.jci.item.azure.data.ResultSet;
import com.jci.item.azure.query.PaginationParam;
import com.jci.item.azure.query.ScrollingParam;
import com.jci.item.dto.SegmentedDetailReq;
import com.jci.item.dto.SegmentedDetailRes;
import com.jci.item.repo.ItemRepo;
import com.jci.item.utils.Constants;
import com.microsoft.azure.storage.StorageException;

/**
 * <p>
 * <strong>The Class ItemServiceImpl.</strong>
 * <p>
 *
 * @author csonisk
 */
@Service
public class ItemServiceImpl implements ItemService{ // NO_UCD (unused code)

	
	/** The repo. */
 @Autowired
	private ItemRepo repo;
	
	
	/* (non-Javadoc)
	 * @see com.jci.item.service.ItemService#getItemResultSet(com.jci.item.dto.SegmentedDetailReq)
	 */
	@Override
	public SegmentedDetailRes getItemResultSet(SegmentedDetailReq request) throws InvalidKeyException, URISyntaxException, StorageException  {
		PaginationParam paginationParam = request.getPaginationParam();
		
		ScrollingParam param  = new ScrollingParam();
		
		if(paginationParam!=null){
			param.setPartition(paginationParam.getNextPartition());
			param.setRow(paginationParam.getNextRow());
		}
		
		//For where condition
		param.setSize(request.getSize());
		
		DataHelper azureRequest = null;
		ResultSet resultSet = null;
		
		SegmentedDetailRes response = new SegmentedDetailRes(); 
		HashMap<String, ResultSet>  resultSetMap = new HashMap<>();
		
		azureRequest = new DataHelper();
		azureRequest.setErpName(request.getErpName());
		azureRequest.setPartitionValue(request.getPartition());
		azureRequest.setTableName(request.getTableName());
		resultSet = repo.getSegmentedResultSet(param, azureRequest);
		resultSetMap.put(request.getErpName(), resultSet);			
		response.setItemData(resultSetMap);
		response.setMessage(Constants.JSON_OK);
		return response;
	}
}
