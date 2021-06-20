package com.sandeera.authserver.Service;

import com.sandeera.authserver.bean.JwkExposedModel;
import com.sandeera.authserver.repository.JwkExposedModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JWKDetailsService {
    @Autowired
    private JwkExposedModelRepository jwkExposedModelRepository;

    public List<JwkExposedModel> findAll() {
        List<JwkExposedModel> allJwks = jwkExposedModelRepository.findAll();
        return allJwks;
    }

    public void addNew(JwkExposedModel newEntry) {
        jwkExposedModelRepository.save(newEntry);
    }
}
