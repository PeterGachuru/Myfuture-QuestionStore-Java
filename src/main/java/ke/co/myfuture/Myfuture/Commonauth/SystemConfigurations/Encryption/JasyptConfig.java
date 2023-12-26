package ke.co.myfuture.Myfuture.Commonauth.SystemConfigurations.Encryption;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jasypt.encryption.StringEncryptor;


@Configuration
public class JasyptConfig {
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPassword("3188b512b4c5ea87f26feaf719aa0bfdd2a251573a6add4d49f2ceda252b24c551f5d74833be388db0d87961f3fa9ec970de57717a3fd379311a76fad0f26137");
        encryptor.setPoolSize(1);
        return encryptor;
    }
}