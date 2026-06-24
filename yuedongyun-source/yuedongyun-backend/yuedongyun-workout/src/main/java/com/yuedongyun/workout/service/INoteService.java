package com.yuedongyun.workout.service;

import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.workout.domain.dto.NoteFormDTO;
import com.yuedongyun.workout.domain.po.Note;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.workout.domain.query.NoteAdminPageQuery;
import com.yuedongyun.workout.domain.query.NotePageQuery;
import com.yuedongyun.workout.domain.vo.NoteAdminDetailVO;
import com.yuedongyun.workout.domain.vo.NoteAdminVO;
import com.yuedongyun.workout.domain.vo.NoteVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 */
public interface INoteService extends IService<Note> {

    void saveNote(NoteFormDTO noteDTO);

    void gatherNote(Long id);

    void removeGatherNote(Long id);

    void updateNote(NoteFormDTO noteDTO);

    PageDTO<NoteVO> queryNotePage(NotePageQuery query);

    PageDTO<NoteAdminVO> queryNotePageForAdmin(NoteAdminPageQuery query);

    NoteAdminDetailVO queryNoteDetailForAdmin(Long id);

    void hiddenNote(Long id, boolean hidden);

    void removeMyNote(Long id);
}

