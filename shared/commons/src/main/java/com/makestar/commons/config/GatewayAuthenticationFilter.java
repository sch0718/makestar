package com.makestar.commons.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 게이트웨이 인증 필터
 * 
 * <p>마이크로서비스에 대한 직접 접근을 방지하고 API 게이트웨이를 통한 요청만 허용합니다.</p>
 * <p>이 필터는 각 마이크로서비스에서 공통으로 사용됩니다.</p>
 * 
 * <p>기능:</p>
 * <ul>
 *   <li>게이트웨이 인증 헤더를 검증합니다.</li>
 *   <li>화이트리스트 IP 주소를 검증합니다.</li>
 *   <li>화이트리스트 URI 경로는 검증을 우회합니다.</li>
 * </ul>
 */
public class GatewayAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(GatewayAuthenticationFilter.class);
    
    @Value("${gateway.security.header-name:X-Gateway-Auth}")
    private String gatewayHeaderName;
    
    @Value("${gateway.security.header-value}")
    private String gatewayHeaderValue;
    
    @Value("${gateway.security.allowed-ips:127.0.0.1}")
    private String allowedIps;
    
    @Value("${gateway.security.whitelist-paths:/actuator/**,/api-docs/**}")
    private String whitelistPaths;
    
    /**
     * 필터 로직 구현
     * 
     * <p>요청이 API 게이트웨이를 통해 왔는지 검증하고, 그렇지 않은 경우 요청을 거부합니다.</p>
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException IO 예외
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // 화이트리스트 경로 검증 우회
        if (isWhitelistedPath(request.getRequestURI())) {
            logger.debug("요청 경로가 화이트리스트에 포함되어 게이트웨이 인증을 우회합니다: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }
        
        // IP 주소 검증
        if (!isAllowedIpAddress(request.getRemoteAddr())) {
            logger.warn("허용되지 않은 IP 주소에서 접근 시도: {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied: Direct access to microservice is not allowed from your IP");
            return;
        }
        
        // 게이트웨이 인증 헤더 검증
        String gatewayHeader = request.getHeader(gatewayHeaderName);
        if (gatewayHeader == null || !gatewayHeader.equals(gatewayHeaderValue)) {
            logger.warn("게이트웨이 인증 헤더 없이 접근 시도: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access denied: Direct access to microservice is not allowed");
            return;
        }
        
        // 모든 검증을 통과하면 요청 진행
        filterChain.doFilter(request, response);
    }
    
    /**
     * 요청 경로가 화이트리스트에 포함되는지 확인
     * 
     * @param requestUri 요청 URI
     * @return 화이트리스트에 포함되면 true, 그렇지 않으면 false
     */
    private boolean isWhitelistedPath(String requestUri) {
        List<String> whitelist = Arrays.asList(whitelistPaths.split(","));
        return whitelist.stream().anyMatch(path -> {
            // '**' 와일드카드 처리
            if (path.endsWith("/**")) {
                String prefix = path.substring(0, path.length() - 3);
                return requestUri.startsWith(prefix);
            }
            return path.equals(requestUri);
        });
    }
    
    /**
     * IP 주소가 허용 목록에 포함되는지 확인
     * 
     * @param ipAddress 클라이언트 IP 주소
     * @return 허용 목록에 포함되면 true, 그렇지 않으면 false
     */
    private boolean isAllowedIpAddress(String ipAddress) {
        List<String> allowedIpList = Arrays.asList(allowedIps.split(","));
        return allowedIpList.contains(ipAddress);
    }
} 