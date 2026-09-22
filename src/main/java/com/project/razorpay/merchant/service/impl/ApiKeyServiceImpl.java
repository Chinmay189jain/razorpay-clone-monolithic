package com.project.razorpay.merchant.service.impl;

import com.project.razorpay.common.exception.ResourceNotFoundException;
import com.project.razorpay.common.util.RandomizerUtil;
import com.project.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.project.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.project.razorpay.merchant.dto.response.ApiKeyResponse;
import com.project.razorpay.merchant.entity.ApiKey;
import com.project.razorpay.merchant.entity.Merchant;
import com.project.razorpay.merchant.repository.ApiKeyRepository;
import com.project.razorpay.merchant.repository.MerchantRepository;
import com.project.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        // Generates a string with
        String keyId = "rzp_"+request.environment().name().toLowerCase()+"_"+RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(rawSecret) // TODO: Encrypt the secret key
                .environment(request.environment())
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId) {
        return apiKeyRepository.findByMerchant_Id(merchantId).stream()
                .map(apiKey ->
                        new ApiKeyResponse(
                                apiKey.getId(),
                                apiKey.getKeyId(),
                                apiKey.getEnvironment(),
                                apiKey.isEnabled(),
                                apiKey.getLastUsedAt(),
                                null
                        ))
                .toList();
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .filter(apiKey -> apiKey.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("apiKey", keyId));

        key.setEnabled(false);
    }

    @Override
    @Transactional
    public @Nullable ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId) {
        ApiKey key = apiKeyRepository.findById(keyId)
                .filter(apiKey -> apiKey.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("apiKey", keyId));

        if(!key.isEnabled()) throw new RuntimeException("Cannot rotate a disabled key");

        String newRawSecret = RandomizerUtil.randomBase64(40);
        key.setPreviousKeySecretHash(key.getKeySecretHash());
        key.setKeySecretHash(newRawSecret); // TODO: Encrypt the secret key
        key.setRotatedAt(LocalDateTime.now());
        key.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        key = apiKeyRepository.save(key);

        return new ApiKeyCreateResponse(key.getId(), key.getKeyId(), newRawSecret, key.getEnvironment());
    }
}
