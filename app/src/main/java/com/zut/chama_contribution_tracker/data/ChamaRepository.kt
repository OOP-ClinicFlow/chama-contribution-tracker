package com.zut.chama_contribution_tracker.data

import kotlinx.coroutines.flow.Flow

class ChamaRepository(
    private val memberDao: MemberDao,
    private val contributionDao: ContributionDao
) {

    fun getAllMembers(): Flow<List<Member>> = memberDao.getAllMembers()

    suspend fun insertMember(member: Member): Long = memberDao.insert(member)

    suspend fun getMemberById(id: Long): Member? = memberDao.getMemberById(id)

    fun getAllContributions(): Flow<List<Contribution>> = contributionDao.getAllContributions()

    fun getContributionsByMember(memberId: Long): Flow<List<Contribution>> =
        contributionDao.getContributionsByMember(memberId)

    suspend fun insertContribution(contribution: Contribution): Long =
        contributionDao.insert(contribution)

    suspend fun getTotalForMember(memberId: Long): Double =
        contributionDao.getTotalForMember(memberId)

    suspend fun getGroupTotal(): Double = contributionDao.getGroupTotal()
}