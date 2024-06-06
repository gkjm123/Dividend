package com.example.dividend.service;

import com.example.dividend.exception.impl.NoCompanyException;
import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.persist.entity.MemberEntity;
import com.example.dividend.scraper.Scraper;
import com.example.dividend.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = companyRepository.existsByTicker(ticker);

        if (exists) {
            throw new RuntimeException("already existed ticker: " + ticker);
        }

        return storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        Company company = yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap company");
        }

        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        CompanyEntity companyEntity = companyRepository.save(CompanyEntity.builder()
                .name(company.getName())
                .ticker(company.getTicker())
                .build());

        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(companyEntity.getId(), e))
                .toList();

        dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public void addAutocompleteKeyword(String keyword) {
        trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return (List<String>) trie.prefixMap(keyword).keySet().stream().toList();
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                .map(e -> e.getName())
                .toList();
    }

    public void deleteAutocompleteKeyword(String keyword) {
        trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow( () -> new NoCompanyException());

        dividendRepository.deleteAllByCompanyId(companyEntity.getId());
        companyRepository.delete(companyEntity);

        return companyEntity.getName();
    }
}
