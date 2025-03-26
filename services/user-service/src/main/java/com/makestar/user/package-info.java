/**
 * 사용자 서비스 패키지
 * 
 * <p>
 * 이 패키지는 MakeStar 채팅 애플리케이션의 사용자 관련 기능을 제공하는 마이크로서비스입니다.
 * </p>
 * 
 * <h2>주요 기능:</h2>
 * <ul>
 *   <li>사용자 프로필 관리
 *     <ul>
 *       <li>사용자 정보 생성, 조회, 수정, 삭제</li>
 *       <li>사용자 상태 관리 (온라인, 오프라인, 자리비움 등)</li>
 *     </ul>
 *   </li>
 *   <li>친구 관계 관리
 *     <ul>
 *       <li>친구 목록 관리</li>
 *       <li>친구 요청 처리</li>
 *     </ul>
 *   </li>
 *   <li>사용자 검색 및 조회
 *     <ul>
 *       <li>사용자명 기반 검색</li>
 *       <li>ID 기반 조회</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h2>패키지 구조:</h2>
 * <ul>
 *   <li>controller: REST API 엔드포인트 정의</li>
 *   <li>service: 비즈니스 로직 구현</li>
 *   <li>repository: 데이터 접근 계층</li>
 *   <li>model: 도메인 모델 클래스</li>
 *   <li>dto: 데이터 전송 객체</li>
 * </ul>
 *
 * @since 1.0.0
 * @see com.makestar.userservice.model.User
 * @see com.makestar.userservice.model.FriendRequest
 */
package com.makestar.user;
