package com.tagai.data.mapper

import com.tagai.data.local.entity.NoteEntity
import com.tagai.domain.model.Note

fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        tags = if (tags.isBlank()) emptyList() else tags.split(","),
        timestamp = timestamp
    )
}

fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        tags = tags.joinToString(","),
        timestamp = timestamp
    )
}
