/*
 * Copyright (c) 2013 L2jBAN-JDEV
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package quests.Q10442_MemoryOfTheGloriousPast2;

import net.sf.l2jdev.gameserver.data.xml.TeleportListData;
import net.sf.l2jdev.gameserver.model.Location;
import net.sf.l2jdev.gameserver.model.actor.Npc;
import net.sf.l2jdev.gameserver.model.actor.Player;
import net.sf.l2jdev.gameserver.model.events.EventType;
import net.sf.l2jdev.gameserver.model.events.ListenerRegisterType;
import net.sf.l2jdev.gameserver.model.events.annotations.RegisterEvent;
import net.sf.l2jdev.gameserver.model.events.annotations.RegisterType;
import net.sf.l2jdev.gameserver.model.events.holders.actor.player.OnPlayerQuestComplete;
import net.sf.l2jdev.gameserver.model.script.Quest;
import net.sf.l2jdev.gameserver.model.script.QuestDialogType;
import net.sf.l2jdev.gameserver.model.script.QuestState;
import net.sf.l2jdev.gameserver.model.script.newquestdata.NewQuest;
import net.sf.l2jdev.gameserver.model.script.newquestdata.NewQuestLocation;
import net.sf.l2jdev.gameserver.model.script.newquestdata.QuestCondType;
import net.sf.l2jdev.gameserver.network.serverpackets.quest.ExQuestDialog;
import net.sf.l2jdev.gameserver.network.serverpackets.quest.ExQuestNotification;

import quests.Q10340_MemoryOfTheGloriousPast3.Q10340_MemoryOfTheGloriousPast3;

/**
 * @author Magik
 */
public class Q10442_MemoryOfTheGloriousPast2 extends Quest
{
	private static final int QUEST_ID = 10442;
	private static final int[] MONSTERS =
	{
		20666, // 타이크 오크 감시병
		20667, // 파크란
		20668, // 그레이브 가드
		20669, // 타이크 오크 보급장교
		20670, // 크림슨 드레이크
		20671, // 카디오스
		20672, // 트리베스
		20673, // 팔리바티
		20674, // 둠 나이트
		20675, // 타이림
		20676, // 늪의 심판관
		20677, // 툴벤
		20678, // 불사의 형벌
		20679, // 마쉬 스토커
		20680, // 마쉬 드레이크
		20681, // 바노르 실레노스
		20682, // 바노르 실레노스 병사
		20683, // 바노르 실레노스 정찰병
		20684, // 바노르 실레노스 전사
		20685, // 바노르 실레노스 주술사
		20686, // 바노르 실레노스 족장

		20928, // 하투 위어드 비
		20929, // 하투 다이어 울프
		20930, // 하투 브라운 베어
		20931, // 하투 오닉스 비스트
		20932, // 하투 크림슨 베어
		20933, // 하투 윈드수스
		20934, // 말벌 일꾼
		20935, // 말벌 대장
		20936, // 타노르 실레노스
		20937, // 타노르 실레노스 병사
		20938, // 타노르 실레노스 정찰병
		20939, // 타노르 실레노스 전사
		20940, // 타노르 실레노스 주술사
		20941, // 타노르 실레노스 족장
		20942, // 악몽의 안내자
		20943, // 악몽의 감시자
		20944, // 악몽의 지배자
		20945, // 카데인
		20946, // 산히드로
		20947, // 코나비
		20948, // 바르탈
		20949, // 루미넌
		20950, // 이너센

		24014, // 바노르
		24015, // 바리프의 감린
		24016, // 퇴화된 하거인 지휘관
		24017, // 뉴크
		24018, // 헤카톤 프라임
		24019, // 헤카톤 키레스
		24020, // 바투르
		24021, // 퇴화된 하거인 전사
		24022, // 퇴화된 하거인 마법사
		24023, // 뉴크 헌터
		24024, // 헌터

		22101, // 버처 가디언
		22102, // 아너 가디언
		22103, // 피어스 가디언

		23014, // 앤트
		23015, // 데빌
		23016, // 스타크
		23017, // 하운드
	};

	public Q10442_MemoryOfTheGloriousPast2()
	{
		super(QUEST_ID);
		addKillId(MONSTERS);
	}

	@Override
	public String onEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "ACCEPT":
			{
				if (!canStartQuest(player))
				{
					break;
				}

				final QuestState questState = getQuestState(player, true);
				if (!questState.isStarted() && !questState.isCompleted())
				{
					questState.startQuest();
				}
				break;
			}
			case "TELEPORT":
			{
				QuestState questState = getQuestState(player, false);
				if (questState == null)
				{
					if (!canStartQuest(player))
					{
						break;
					}

					questState = getQuestState(player, true);

					final NewQuestLocation questLocation = getQuestData().getLocation();
					if (questLocation.getStartLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getStartLocationId()).getLocation();
						if (teleportToQuestLocation(player, location))
						{
							questState.setCond(QuestCondType.ACT);
							sendAcceptDialog(player);
						}
					}
					break;
				}

				final NewQuestLocation questLocation = getQuestData().getLocation();
				if (questState.isCond(QuestCondType.STARTED))
				{
					if (questLocation.getQuestLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getQuestLocationId()).getLocation();
						if (teleportToQuestLocation(player, location) && (questLocation.getQuestLocationId() == questLocation.getEndLocationId()))
						{
							questState.setCond(QuestCondType.DONE);
							sendEndDialog(player);
						}
					}
				}
				else if (questState.isCond(QuestCondType.DONE) && !questState.isCompleted())
				{
					if (questLocation.getEndLocationId() > 0)
					{
						final Location location = TeleportListData.getInstance().getTeleport(questLocation.getEndLocationId()).getLocation();
						if (teleportToQuestLocation(player, location))
						{
							sendEndDialog(player);
						}
					}
				}
				break;
			}
			case "COMPLETE":
			{
				final QuestState questState = getQuestState(player, false);
				if (questState == null)
				{
					break;
				}

				if (questState.isCond(QuestCondType.DONE) && !questState.isCompleted())
				{
					questState.exitQuest(false, true);
					rewardPlayer(player);
				}

				final QuestState nextQuestState = player.getQuestState(Q10340_MemoryOfTheGloriousPast3.class.getSimpleName());
				if (nextQuestState == null)
				{
					player.sendPacket(new ExQuestDialog(10340, QuestDialogType.ACCEPT));
				}
				break;
			}
		}

		return null;
	}

	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final QuestState questState = getQuestState(player, false);
		if ((questState != null) && !questState.isCompleted())
		{
			if ((questState.getCount() == 0) && questState.isCond(QuestCondType.NONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.START));
			}
			else if (questState.isCond(QuestCondType.DONE))
			{
				player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.END));
			}
		}

		npc.showChatWindow(player);
		return null;
	}

	@Override
	public void onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState questState = getQuestState(killer, false);
		if ((questState != null) && questState.isCond(QuestCondType.STARTED))
		{
			final NewQuest data = getQuestData();
			if (data.getGoal().getItemId() > 0)
			{
				final int itemCount = (int) getQuestItemsCount(killer, data.getGoal().getItemId());
				if (itemCount < data.getGoal().getCount())
				{
					giveItems(killer, data.getGoal().getItemId(), 1);
					final int newItemCount = (int) getQuestItemsCount(killer, data.getGoal().getItemId());
					questState.setCount(newItemCount);
				}
			}
			else
			{
				final int currentCount = questState.getCount();
				if (currentCount < data.getGoal().getCount())
				{
					questState.setCount(currentCount + 1);
				}
			}

			if (questState.getCount() >= data.getGoal().getCount())
			{
				questState.setCond(QuestCondType.DONE);
				killer.sendPacket(new ExQuestNotification(questState));
			}
		}
	}

	@RegisterEvent(EventType.ON_PLAYER_QUEST_COMPLETE)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerCompleteQuest(OnPlayerQuestComplete event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}

		final QuestState questState = getQuestState(player, false);
		if ((questState == null) && canStartQuest(player))
		{
			player.sendPacket(new ExQuestDialog(QUEST_ID, QuestDialogType.ACCEPT));
		}
	}
}
