package com.sample.web.base.security;

import static com.sample.web.base.WebConst.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.val;

public abstract class BaseSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${application.security.secureCookie:false}")
    Boolean secureCookie;

    @Value("${application.security.tokenValiditySeconds:86400}")
    Integer tokenValiditySeconds;

    @Autowired
    DataSource dataSource;

    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public PersistentTokenRepository jdbcTokenRepository() {
        // TODO: デバイス単位で記憶するようにする
        val repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        return repository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 静的ファイルへのアクセスは認証をかけない
        web.ignoring().antMatchers(WEBJARS_URL, STATIC_RESOURCES_URL);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CookieにCSRFトークンを保存する
        http.csrf().csrfTokenRepository(new CookieCsrfTokenRepository());

        http.authorizeRequests()
                // エラー画面は認証をかけない
                .antMatchers(FORBIDDEN_URL, ERROR_URL).permitAll()
                // エラー画面以外は、認証をかける
                .anyRequest().authenticated();

        http.formLogin()
                // ログイン画面のURL
                .loginPage(LOGIN_URL)
                // 認可を処理するURL
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                // ログイン成功時の遷移先
                .successForwardUrl(LOGIN_SUCCESS_URL)
                // ログイン失敗時の遷移先
                .failureUrl(LOGIN_FAILURE_URL)
                // ログインIDのパラメータ名
                .usernameParameter("loginId")
                // パスワードのパラメータ名
                .passwordParameter("password").permitAll();

        // ログアウト設定
        http.logout().logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_URL))
                // Cookieを破棄する
                .deleteCookies("SESSION", "rememberMe")
                // ログアウト画面のURL
                .logoutUrl(LOGOUT_URL)
                // ログアウト後の遷移先
                .logoutSuccessUrl(LOGIN_URL)
                // ajaxの場合は、HTTPステータスを返す
                .defaultLogoutSuccessHandlerFor(new HttpStatusReturningLogoutSuccessHandler(),
                        request -> "XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
                // セッションを破棄する
                .invalidateHttpSession(true).permitAll();

        // RememberMe
        http.rememberMe().tokenRepository(jdbcTokenRepository())
                // Cookie名
                .rememberMeCookieName("rememberMe")
                // TODO: 本番ではsecure=trueにする
                .alwaysRemember(true).useSecureCookie(secureCookie)
                // 1日ごとにリフレッシュする
                .tokenValiditySeconds(tokenValiditySeconds);
    }
}
