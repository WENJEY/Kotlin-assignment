create table if not exists public.chat_conversations (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users (id) on delete cascade,
  title text not null default 'New chat',
  updated_at timestamptz not null default timezone('utc'::text, now())
);

create index if not exists chat_conversations_user_updated_idx
  on public.chat_conversations (user_id, updated_at desc);

alter table public.chat_conversations enable row level security;

drop policy if exists "Users can read own chat conversations" on public.chat_conversations;
drop policy if exists "Users can insert own chat conversations" on public.chat_conversations;
drop policy if exists "Users can update own chat conversations" on public.chat_conversations;

create policy "Users can read own chat conversations"
  on public.chat_conversations
  for select
  to authenticated
  using (auth.uid() = user_id);

create policy "Users can insert own chat conversations"
  on public.chat_conversations
  for insert
  to authenticated
  with check (auth.uid() = user_id);

create policy "Users can update own chat conversations"
  on public.chat_conversations
  for update
  to authenticated
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

create table if not exists public.chat_messages (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references auth.users (id) on delete cascade,
  text text not null,
  is_from_user boolean not null,
  created_at timestamptz not null default timezone('utc'::text, now())
);

alter table public.chat_messages
  add column if not exists conversation_id uuid references public.chat_conversations (id) on delete cascade;

create index if not exists chat_messages_user_created_idx
  on public.chat_messages (user_id, created_at);

create index if not exists chat_messages_conversation_created_idx
  on public.chat_messages (conversation_id, created_at);

alter table public.chat_messages enable row level security;

drop policy if exists "Users can read own chat messages" on public.chat_messages;
drop policy if exists "Users can insert own chat messages" on public.chat_messages;

create policy "Users can read own chat messages"
  on public.chat_messages
  for select
  to authenticated
  using (auth.uid() = user_id);

create policy "Users can insert own chat messages"
  on public.chat_messages
  for insert
  to authenticated
  with check (auth.uid() = user_id);

insert into public.chat_conversations (user_id, title)
select distinct m.user_id, 'Previous chat'
from public.chat_messages m
where m.conversation_id is null
  and not exists (
    select 1 from public.chat_conversations c where c.user_id = m.user_id
  );

update public.chat_messages m
set conversation_id = c.id
from public.chat_conversations c
where m.user_id = c.user_id
  and m.conversation_id is null;
