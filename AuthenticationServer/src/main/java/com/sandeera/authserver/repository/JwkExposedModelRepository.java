package com.sandeera.authserver.repository;

import com.sandeera.authserver.bean.JwkExposedModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwkExposedModelRepository  extends JpaRepository<JwkExposedModel,String> {
}
