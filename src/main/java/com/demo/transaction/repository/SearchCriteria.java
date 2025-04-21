package com.demo.transaction.repository;

import com.demo.transaction.enumeration.SearchOperation;


public record SearchCriteria(String key, Object value, SearchOperation operation) {

}