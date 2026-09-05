package com.studying.backendservice.services;

import java.util.List;

public interface SchemaService {
  List<String> getSchemas();
  void saveSchemas();

}
