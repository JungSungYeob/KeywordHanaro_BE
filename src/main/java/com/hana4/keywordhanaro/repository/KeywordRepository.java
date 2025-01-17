package com.hana4.keywordhanaro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hana4.keywordhanaro.model.entity.keyword.Keyword;
import com.hana4.keywordhanaro.model.entity.keyword.KeywordType;
import com.hana4.keywordhanaro.model.entity.user.User;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

	Optional<Keyword> findByName(String name);

	Optional<Keyword> findTopByUserIdOrderBySeqOrderDesc(String userId);

	List<Keyword> findByUserId(String useId);

	Optional<Keyword> findTopByUserIdAndType(String userId, KeywordType type);

	@Query("SELECT DISTINCT k.type FROM Keyword k WHERE k.user.id = :userId")
	List<KeywordType> findTypesByUserId(@Param("userId") String userId);

	List<Keyword> findAllByUserUsername(String username);

	List<Keyword> findAllByUserUsernameAndIsFavoriteTrue(String username);

	Optional<Keyword> findFirstByUserAndType(User user, KeywordType type);
}
