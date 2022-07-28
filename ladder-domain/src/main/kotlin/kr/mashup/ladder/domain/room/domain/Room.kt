package kr.mashup.ladder.domain.room.domain

import kr.mashup.ladder.domain.common.domain.BaseEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.OneToMany
import javax.persistence.Table

@Table(name = "room")
@Entity
class Room(
    @Column(nullable = false)
    var description: String,

    @Embedded
    var invitationKey: InvitationKey,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: RoomStatus = RoomStatus.ACTIVE,
) : BaseEntity() {

    @OneToMany(mappedBy = "room", cascade = [CascadeType.ALL], orphanRemoval = true)
    val participants: MutableList<RoomMemberMapper> = mutableListOf()

    fun update(description: String) {
        this.description = description
    }

    fun delete() {
        this.status = RoomStatus.DELETED
    }

    fun addCreator(memberId: Long) {
        RoomRoleValidator.validateNotParticipant(room = this, memberId = memberId)
        this.participants.add(RoomMemberMapper.newAdmin(room = this, memberId = memberId))
    }

    fun addGuest(memberId: Long) {
        RoomRoleValidator.validateNotParticipant(room = this, memberId = memberId)
        this.participants.add(RoomMemberMapper.newGuest(room = this, memberId = memberId))
    }

    fun getRole(memberId: Long): RoomRole? {
        return findParticipant(memberId)?.role
    }

    fun isCreator(memberId: Long): Boolean {
        return findParticipant(memberId)?.role == RoomRole.CREATOR
    }

    fun isParticipant(memberId: Long): Boolean {
        return findParticipant(memberId) != null
    }

    private fun findParticipant(memberId: Long): RoomMemberMapper? {
        return this.participants.find { participant -> participant.memberId == memberId }
    }

    fun getCreator(): Long {
        return this.participants.find { participant -> participant.role == RoomRole.CREATOR }?.memberId
            ?: throw IllegalArgumentException("방(${id})에 대한 생성자가 존재하지 않습니다")
    }

    companion object {
        fun newInstance(description: String, memberId: Long): Room {
            val room = Room(
                description = description,
                invitationKey = InvitationKey.newInstance()
            )
            room.addCreator(memberId)
            return room
        }
    }

}
