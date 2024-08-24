package com.enthusiasm.plurelogger.log.logger;

import java.io.File;

import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.event.*;

import net.minecraft.server.network.ServerPlayerEntity;

import com.enthusiasm.plurecore.utils.FileUtils;
import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurelogger.helper.DateHelper;
import com.enthusiasm.plurelogger.helper.IOHelper;
import com.enthusiasm.plurelogger.log.AbstractLogger;

public class FtbTeamsLogger extends AbstractLogger {
    private final String rootLogDir;
    private String ftbTeamsLogDir;

    public FtbTeamsLogger(String rootLogDir) {
        this.rootLogDir = rootLogDir;
    }

    @Override
    public void init() {
        File teamsDir = new File(this.rootLogDir, "ftbteams");
        FolderUtils.createFolderAsync(String.valueOf(teamsDir));

        this.ftbTeamsLogDir = teamsDir.getAbsolutePath();
    }

    @Override
    public void subscribeToEvent() {
        TeamEvent.PLAYER_JOINED_PARTY.register(this::handlePlayerJoinEvent);
        TeamEvent.PLAYER_LEFT_PARTY.register(this::handlePlayerLeaveEvent);
        TeamEvent.CREATED.register(this::handleTeamCreateEvent);
        TeamEvent.DELETED.register(this::handleTeamDeleteEvent);
        TeamEvent.OWNERSHIP_TRANSFERRED.register(this::handleTransferOwnershipEvent);
        TeamEvent.ADD_ALLY.register(this::handleAllyEvent);
        TeamEvent.REMOVE_ALLY.register(this::handleAllyEvent);
    }

    private void handlePlayerJoinEvent(PlayerJoinedPartyTeamEvent playerJoinedPartyTeamEvent) {
        ServerPlayerEntity sourcePlayer = playerJoinedPartyTeamEvent.getPlayer();
        Team team = playerJoinedPartyTeamEvent.getTeam();

        if (team.getOwner().equals(sourcePlayer.getUuid())) {
            return;
        }

        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile("player", this.ftbTeamsLogDir, DateHelper.SHORT_PATTERN_DATE, true);

        String content = String.format(
                "[%s] Игрок %s присоединился к команде %s.\n",
                formattedDate,
                sourcePlayer.getEntityName(),
                team.getName()
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
    private void handlePlayerLeaveEvent(PlayerLeftPartyTeamEvent playerLeftPartyTeamEvent) {
        ServerPlayerEntity sourcePlayer = playerLeftPartyTeamEvent.getPlayer();
        Team team = playerLeftPartyTeamEvent.getTeam();

        if (!team.isValid()) {
            return;
        }

        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile("player", this.ftbTeamsLogDir, DateHelper.SHORT_PATTERN_DATE, true);

        String content = String.format(
                "[%s] Игрок %s покинул команду %s.\n",
                formattedDate,
                sourcePlayer.getEntityName(),
                team.getName()
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
    private void handleTeamCreateEvent(TeamCreatedEvent teamCreatedEvent) {
        ServerPlayerEntity sourcePlayer = teamCreatedEvent.getCreator();
        Team team = teamCreatedEvent.getTeam();

        if (team.isPlayerTeam()) {
            return;
        }

        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile("team", this.ftbTeamsLogDir, DateHelper.SHORT_PATTERN_DATE, true);

        String content = String.format(
                "[%s] Игрок %s создал команду %s.\n",
                formattedDate,
                sourcePlayer.getEntityName(),
                team.getName()
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
    private void handleTeamDeleteEvent(TeamEvent teamEvent) {
        Team team = teamEvent.getTeam();

        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile("team", this.ftbTeamsLogDir, DateHelper.SHORT_PATTERN_DATE, true);

        String content = String.format(
                "[%s] Команда %s расформирована.\n",
                formattedDate,
                team.getName()
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
    private void handleTransferOwnershipEvent(PlayerTransferredTeamOwnershipEvent transferredTeamOwnershipEvent) {
        Team team = transferredTeamOwnershipEvent.getTeam();
        ServerPlayerEntity from = transferredTeamOwnershipEvent.getFrom();
        ServerPlayerEntity to = transferredTeamOwnershipEvent.getTo();

        if (from == null || to == null) {
            return;
        }

        String formattedDate = DateHelper.getDate();

        File logFile = IOHelper.initLogFile("team", this.ftbTeamsLogDir, DateHelper.SHORT_PATTERN_DATE, true);

        String content = String.format(
                "[%s] Игрок %s передал право владения командой %s игроку %s.\n",
                formattedDate,
                from.getEntityName(),
                team.getName(),
                to.getEntityName()
        );

        FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
    }
    private void handleAllyEvent(TeamAllyEvent teamAllyEvent) {
        Team team = teamAllyEvent.getTeam();

        String formattedDate = DateHelper.getDate();
        File logFile = IOHelper.initLogFile("ally", this.ftbTeamsLogDir, DateHelper.SHORT_PATTERN_DATE, true);

        teamAllyEvent.getPlayers().forEach((gameProfile -> {
            String content = String.format(
                    "[%s] Игрок %s %s в союзе с командой %s.\n",
                    formattedDate,
                    gameProfile.getName(),
                    teamAllyEvent.isAdding() ? "тепер" : "больше не",
                    team.getName()
            );

            FileUtils.writeFileAsync(logFile.toPath().toAbsolutePath(), content);
        }));
    }
}
