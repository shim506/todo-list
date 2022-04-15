package team03.todoapp.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team03.todoapp.controller.CardLocation;
import team03.todoapp.repository.domain.Card;

@Repository
public class CardRepository {

    private final Logger log = LoggerFactory.getLogger(CardRepository.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public CardRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("card")
            .usingGeneratedKeyColumns("card_id");
    }

    @Transactional
    public Long insert(Card card) {
        /**
         *
         * select card_id from card where next_id = null and current_location = "Todo";
         * int lastCardId = cardId; if lastCardId==null; insert 후 return returnKey;
         *
         * insert한 후의 키값을 반환받아서 -> int thisCardId = returnKey;
         * card 테이블 done 속성 컬럼 중 마지막 컬럼의 next_id로 할당해줘야함
         *
         * update card
         * set next_id=thisCardId
         * where card_id=lastCardId;
         */

        String getLastCardIdSQL = "select card_id from card where next_id is null and current_location = ?";
        String updateNextCardOfLastCardSQL = "update card set next_id = ? where card_id = ?";

        Integer lastCardId = DataAccessUtils.singleResult(
            jdbcTemplate.queryForList(getLastCardIdSQL, Integer.class,
                card.getCurrentLocation().name()));

        Map<String, Object> params = new HashMap<>();
        params.put("title", card.getTitle());
        params.put("content", card.getContent());
        params.put("writer", card.getWriter());
        params.put("current_location", card.getCurrentLocation().name());
        params.put("upload_date", card.getUploadDateTime());
        params.put("is_deleted", card.getIsDeleted());
        params.put("next_id", null);
        Long thisCardId = simpleJdbcInsert.executeAndReturnKey(params).longValue();

        if (lastCardId != null) {
            jdbcTemplate.update(updateNextCardOfLastCardSQL, thisCardId, lastCardId);
        }
        return thisCardId;
    }

    @Transactional
    public void delete(Long cardId) {
        /**
         * 연결리스트 형식 테이블의 삭제 과정
         *  1. select로 cardId를 next_id로 갖고있는 column의 card_id를 Long prevId에 저장
         *  2. select로 cardId column의 next_id를 Long nextId에 저장
         *  3. delete로 cardId column 삭제
         *  4. prevId column의 next_id를 nextId로 할당
         *
         *  edge-cases :
         *  1. 마지막 원소를 삭제하는 경우 (문제있음)
         *  2. 첫번째 원소를 삭제하는경우 (if prevId!=null: update하기)
         *  3. 중간 원소를 삭제하는 경우 (문제없음)
         *  4. 원소가 하나일 때 삭제하는 경우 (prevId=null, nextId=null)->(if prevId!=null: update하기)
         */

        Long prevId = null;
        Long nextId = null;
        String getBeforeCardId = "select card_id from card where next_id = ?";
        String getNextCardId = "select next_id from card where card_id = ?" + cardId;
        String deleteCurrentCard = "delete from card where card_id = ?";
        String updateCardRelations = "update card set next_id = ? where card_id = ?";

        try {
            prevId = jdbcTemplate.queryForObject(getBeforeCardId, Long.class, cardId);
            log.debug("prevId: {}", prevId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("e: {}", e);
        }

        try {
            nextId = jdbcTemplate.queryForObject(getNextCardId, Long.class, cardId);
            log.debug("nextId: {}", nextId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("e: {}", e);
        }

        jdbcTemplate.update(deleteCurrentCard, cardId);

        if (prevId != null) {
            log.debug("prev{} next{}", prevId, nextId);
            jdbcTemplate.update(updateCardRelations, nextId, prevId);
        }
        log.debug("cardId: {} deleted", cardId);
    }

    public void updateNextIdByCardId(Long nextId, Long cardId) {
        String SQL = "update card set next_id = ? where card_id = ?";
        jdbcTemplate.update(SQL, nextId, cardId);
    }

    public void updateNextIdAndLocationByCardId(Long nextId, String Location, Long cardId) {
        String SQL = "update card set next_id = ?, current_location = ? where card_id = ?";
        jdbcTemplate.update(SQL, nextId, Location, cardId);
    }

    public Long findCardIdByNextId(Long cardId) {
        String SQL = "select card_id from card where next_id = ?";
        Long findCardId = null;
        try {
            findCardId = jdbcTemplate.queryForObject(SQL, Long.class, cardId);
        } catch (EmptyResultDataAccessException e) { // 반환값이 없으면 findCardId null 유지
            log.debug("empty beforePrevId :{}", e);
        }
        return findCardId;
    }

    public Long findNextIdByCardId(Long cardId) {
        String SQL = "select next_id from card where card_id = ?";
        Long findNextId = null;
        try {
            findNextId = jdbcTemplate.queryForObject(SQL, Long.class, cardId);
        } catch (EmptyResultDataAccessException e) { // 반환값이 없으면 findNextId null 유지
            log.debug("empty beforePrevId :{}", e);
        }
        return findNextId;
    }

    public void update(Card card) {
        jdbcTemplate.update(
            "update card set title = ?, content = ? where card_id = ? and is_deleted = false",
            card.getTitle(), card.getContent(), card.getId());
    }

    public Optional<Card> findById(Long cardId) {

        String sql = "select card_id, title, content, writer, current_location, upload_date, next_id, is_deleted from card where card_id = ? and is_deleted = false";

        Card card = DataAccessUtils.singleResult(
            jdbcTemplate.query(sql, getCardRowMapper(), cardId));

        return Optional.ofNullable(card);
    }

    public List<Card> findAll() {
        String sql = "select card_id, title, content, writer, current_location, upload_date, next_id, is_deleted from card where is_deleted = false";
        return jdbcTemplate.query(sql, getCardRowMapper());
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from card");
    }

    private RowMapper<Card> getCardRowMapper() {
        return (rs, rowNum) ->
            new Card(
                rs.getLong("card_id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("writer"),
                CardLocation.valueOf(rs.getString("current_location")),
                rs.getObject("upload_date", LocalDateTime.class),
                rs.getLong("next_id"));
    }

}