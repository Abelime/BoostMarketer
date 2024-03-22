package camel.BoostMarketer.keyword.service;

import camel.BoostMarketer.keyword.mapper.KeywordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordMapper keywordMapper;


}
