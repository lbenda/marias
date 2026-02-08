import { test, expect } from '@playwright/test';

test('create game and load game page', async ({ page }) => {
  // Go to home page
  await page.goto('/');

  // Enter name
  await page.fill('input', 'TestPlayer');

  // Click Create Game
  await page.click('button:has-text("Create Game")');

  // Should navigate to /game/ID
  await expect(page).toHaveURL(/\/game\/.+/, { timeout: 15000 });

  // Wait for loading to finish (should see "Game" header)
  const gameHeader = page.locator('h2', { hasText: /Game/ });
  await expect(gameHeader).toBeVisible({ timeout: 15000 });

  // Check if phase is WAITING_FOR_PLAYERS
  await expect(page.getByText('Phase: WAITING_FOR_PLAYERS')).toBeVisible();

  // Check if player list contains our name
  // Use a more specific locator to avoid strict mode violation
  // The div containing "Players:" also contains the player names
  await expect(page.locator('div', { hasText: /^Players:/ }).getByText('TestPlayer')).toBeVisible();

  // Ensure no error message is visible
  const errorMsg = page.locator('text=Error:');
  await expect(errorMsg).not.toBeVisible();
});
