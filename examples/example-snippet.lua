-- This is a uncommented example snippet from my own avatar, it wont work by itself and needs adjustment

local UPDATE_RATE = 15
local ALPHA = 2 / (UPDATE_RATE + 1)
local smoothedVolume = 0

local function EMA(newVolume)
	smoothedVolume = ALPHA * newVolume + (1 - ALPHA) * smoothedVolume
	return smoothedVolume
end

if DAF.init() then
	local voicechat = DAF.events

	voicechat.setAudioModifer(function(audio)
		return svc:amplify(audio, 2)
	end)

	local ticker = 1
	local originalPos = modelRoot.body.head.LowerMouth:getRot().x

	voicechat.setAnimation(function(audio)
		pcall(function()
			local sum = 0
			local count = #audio
	
			for i = 1, count do
					sum = sum + audio[i]
			end
			local smoothed = -EMA(math.clamp(sum / count, 0, 400) * 0.3)
			if modelRoot.body.head.LowerMouth then
				modelRoot.body.head.LowerMouth:setRot(originalPos+smoothed * 0.3,0,0)
				modelRoot.body.head.upper_muzzle:setRot(-smoothed * 0.05,0,0)
			end
		end)
	end)

	voicechat.setOnMicrophoneOff(function()
		modelRoot.body.head.LowerMouth:setRot(originalPos, 0, 0)
	end)

	voicechat.setFallbackPing(pings.talking)
end
