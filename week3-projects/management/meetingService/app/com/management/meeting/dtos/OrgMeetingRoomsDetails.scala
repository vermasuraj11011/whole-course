package com.management.meeting.dtos

import com.management.meeting.entity.MeetingRoom

case class OrgMeetingRoomsDetails(
  id: Int,
  organizationName: String,
  organizationId: Int,
  meetingRooms: List[MeetingRoom],
  systemAdminId: Int
) {}
