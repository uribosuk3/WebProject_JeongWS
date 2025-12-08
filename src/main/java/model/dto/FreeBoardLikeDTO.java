package model.dto;

public class FreeBoardLikeDTO {

    private int idx;         // 추천 고유 번호 (PK, 시퀀스)
    private int board_idx;   // 추천된 게시글 번호 (FK)
    private int user_idx;    // 추천한 사용자 번호 (FK)

    // Getter와 Setter
    public int getIdx() { return idx; }
    public void setIdx(int idx) { this.idx = idx; }

    public int getBoard_idx() { return board_idx; }
    public void setBoard_idx(int board_idx) { this.board_idx = board_idx; }

    public int getUser_idx() { return user_idx; }
    public void setUser_idx(int user_idx) { this.user_idx = user_idx; }
}